package sk.is.urso.reg.sql;

import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.alfa.utils.SearchUtils.sanitizeValue;


/**
 * Náš helper pre skladanie query
 *
 */
public class Sql implements QueryIHasParameters {
	private static final String SUBQUERY_ALIAS = "sub";
	private static final String INNER = "inner";
	private IsAliasIHasParameters from;
	private StringBuilder where = null;
	private StringBuilder orderBy = null;
	private final List<ValueIHasParameters> selectValues = new ArrayList<>();
	private final List<Join> joins = new ArrayList<>();
	private final List<Field> groupBy = new ArrayList<>();
	private final Map<Class<?>, String> entityToTableName = new HashMap<>();
	private final Set<String> aliases = new LinkedHashSet<>();
	private final List<ConditionIHasParameters> conditions = new ArrayList<>();
	
	public Sql(Class<?> ... entities) {
		for(Class<?>  e : entities) {
			defineEntity(e);
		}
	}

	public Sql(Sql sql) {
		this.aliases.addAll(sql.aliases);
		this.conditions.addAll(sql.conditions);
		this.entityToTableName.putAll(sql.entityToTableName);
		this.from = sql.from;
		this.groupBy.addAll(sql.groupBy);
		this.joins.addAll(sql.joins);
		this.orderBy = optStringBuilder(sql.orderBy);
		this.selectValues.addAll(sql.selectValues);
		if(sql.where != null)
			this.where = new StringBuilder(sql.where);
	}

	private StringBuilder optStringBuilder(StringBuilder orig) {
		if(orig == null) {
			return null;
		}
		return new StringBuilder(orig);
	}
	
	public Alias select(@NonNull ValueIHasParameters field, @NonNull IsAliasIHasParameters alias) {
		this.from = alias;
		this.selectValues.add(field);
		return alias.asAlias();
	}
	
	public Alias select(@NonNull String string, @NonNull Class<?> entity) {
		var alias = nameAndAddNewAlias(entity);
		return select(new Field(alias, string), alias);
	}
	
	/**
	 * Vytvorí select zo subquery
	 * @param string selectované polia
	 * @param subQuery sub query
	 * @return alias ktorý bol subquery pridelený
	 */
	public SubSelect select(@NonNull String string, Sql subQuery) {
		var alias = nameAndAddNewAlias(SUBQUERY_ALIAS);
		var namedQuery = new SubSelect(subQuery, alias);
		this.selectValues.add(new Field(null, string));
		this.from = namedQuery;
		return namedQuery;
	}

	public JoinIncomplete join(@NonNull Alias source, @NonNull Class<?> target) {
		var alias = nameAndAddNewAlias(target);
		var join = new Join(source, alias, INNER);
		this.joins.add(join);
		return new JoinIncomplete(join);
	}

	public JoinIncomplete join(@NonNull Alias source, @NonNull Class<?> target, String type) {
		var alias = nameAndAddNewAlias(target);
		var join = new Join(source, alias, type);
		this.joins.add(join);
		return new JoinIncomplete(join);
	}

	private Alias nameAndAddNewAlias(@NonNull Class<?> entity) {
		String table = table(entity);
		return nameAndAddNewAlias(table, entity);
	}
	
	private Alias nameAndAddNewAlias(@NonNull String table) {
		return nameAndAddNewAlias(table, null);
	}
	
	private Alias nameAndAddNewAlias(@NonNull String table, Class<?> entity) {
		String alias = table;
		var i = 2;
		while(this.aliases.contains(alias)) {
			alias = table + i;
			i++;
		}
		addNewAlias(alias);
		return new Alias(entity, table, alias);
	}
	
	private String defineEntity(Class<?> entity) {
		var e = entity.getAnnotation(Entity.class);
		String tableName = e.name();
		entityToTableName.put(entity, tableName);
		return tableName;
	}
	
	private String table(Class<?> entity) {
		String tableName = this.entityToTableName.get(entity);
		if(tableName == null) {
			tableName = defineEntity(entity);
		}
		return tableName;
	}
	
	private void addNewAlias(String alias) {
		var isNew = aliases.add(alias);
		if(!isNew) {
			throw new IllegalStateException("Alias '" + alias + "' already exists!");
		}
		
	}

	public ConditionIHasParameters eq(Alias alias, String name, Object value) {
		return new Eq(alias, name, value);
	}
	
	public ConditionIHasParameters like(Alias alias, String name, String value) {
		return new Like(alias, name, value);
	}
	
	public ConditionIHasParameters notEq(Alias alias, String name, Object value) {
		return new NotEq(alias, name, value);
	}

	public ConditionIHasParameters or(ConditionIHasParameters c1, ConditionIHasParameters c2) {
		return new Or(c1, c2);
	}
	
	public ConditionIHasParameters or(ConditionIHasParameters... conditions) {
		return new Or(conditions);
	}
	
	public ConditionIHasParameters and(ConditionIHasParameters c1, ConditionIHasParameters c2) {
		return new And(c1, c2);
	}

	public ConditionIHasParameters and(List<ConditionIHasParameters> conditions) {
		return new And(conditions);
	}

	public ConditionIHasParameters or(List<ConditionIHasParameters> conditions) {
		return new Or(conditions);
	}

	public Sql orderBy(Alias alias, String field, boolean ascending) {
		String prefix;
		if(this.orderBy == null) {
			this.orderBy = new StringBuilder();
			prefix = " order by ";
		}
		else {
			prefix = ", ";
		}
		this.orderBy.append(prefix);
		this.orderBy.append(alias.field(field));
		this.orderBy.append(ascending ? " asc ": " desc ");
		return this;
	}

	public Sql where(ConditionIHasParameters condition) {
		this.conditions.add(condition);
		if(this.where == null) {
			this.where = new StringBuilder();
			this.where.append(" where ");
		}
		else {
			this.where.append(" and ");
		}
		this.where.append(condition.toString());
		return this;
	}

	/**
     * Vysklada like predikat s ignorovanim diakritiky a case
     * @param source
     * @param field
     * @param value
     * @return
     */
    public ConditionIHasParameters likeSanitizedValue(Alias source, String field, String value) {
        return like(source, field, "%" + sanitizeValue(value) + "%");
    }

	/**
	 * Vysklada like starts with predikat s ignorovanim diakritiky a case
	 */
	public ConditionIHasParameters likeSanitizedValueStartsWith(Alias source, String field, String value) {
		return like(source, field, sanitizeValue(value) + "%");
	}

    /**
     * Vysklada eq predikat s ignorovanim diakritiky a case
     * @param source
     * @param field
     * @param value
     * @return
     */
    public ConditionIHasParameters eqSanitizedValue(Alias source, String field, String value) {
        return eq(source, field, sanitizeValue(value));
    }

    /**
     * Vyskladá or medzi viacrými podmienkami hodnôt.
     * Pre každú hodnotu sa výskladá jedna podmienka volaním funkcie
     * Ak je hodnota len jedna tak as or vynechá
     * @param values zoznam hodnot ktore ideme orovat
     * @param conditionBuilder funkcia na vytvorenie podmienky z hodnoty
     * @return
     */
	public <T> ConditionIHasParameters or(List<T> values, Function<T, ConditionIHasParameters> conditionBuilder) {
		if(values.size() == 1) {
			return conditionBuilder.apply(values.get(0));
		}
		List<ConditionIHasParameters> comparisons = values.stream().map(conditionBuilder::apply).collect(Collectors.toList());
		return new Or(comparisons);
	}

	public void setParameters(Query query) {
		var index = 1;
		for(Object o : this.getParameters()) {
			query.setParameter(index, o);
			index++;
		}
	}
	
	/**
	 * Vrati parametre ktore sa pouzivaju v SQL prikaze. 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getParameters() {
		return getParameters(Collections.singleton(this.from), this.joins, this.conditions);
	}

	public String toDebugString() {
		return toString() + ", params: " + Arrays.toString(getParameters());
	}

	@Override
	public String getSql() {
		return toString();
	}
	
	@Override
	public String toString() {
		var sb = new StringBuilder();
		appendSelect(sb);
		if(this.from != null) {
			sb.append(" from ");
			sb.append(this.from.toString());
		}
		for (Join join : this.joins) {
			sb.append(join.toString());
		}
		if(this.where != null) {
			sb.append(this.where);
		}
		if(!groupBy.isEmpty()) {
			sb.append(" group by ");
			var first = true;
			for(Field field : this.groupBy) {
				if(!first) {
					sb.append(", ");
				}
				first = false;
				sb.append(field.toString());
			}
		}
		if(this.orderBy != null) {
			sb.append(this.orderBy);
		}
		return sb.toString();
	}

	private void appendSelect(StringBuilder sb) {
		if(!this.selectValues.isEmpty()) {
			sb.append(" select ");
			var first = true;
			for(var value : this.selectValues) {
				if(!first) {
					sb.append(", ");
				}
				first = false;
				sb.append(value.toString());
			}
		}
	}

	public void groupBy(Alias alias, String entryId) {
		this.groupBy.add(new Field(alias, entryId));
	}
	
	/**
	 * Vrati kopiu tejto query upravenu ako count query.
	 * Upravenie znamena pouzitie count(*) alebo count(aktualnyselect), odstranenie orderBy.
	 * @return
	 */
	public @NonNull Sql asCountQuery() {
		if(selectValues.isEmpty()) {
			throw new IllegalStateException("Query neobsahuje select hodnoty! Bez toho nevieme spraviť count query!");
		}
		var sql = new Sql(this);
		ValueIHasParameters field;
		if(selectValues.size() == 1) {
			var selectedValue = selectValues.get(0);
			field = new sk.is.urso.reg.sql.Function("count", selectedValue);
		}
		else {
			field = new sk.is.urso.reg.sql.Function("count", new Literal("*"));
		}
		sql.selectValues.clear();
		sql.select(field, sql.from);
		sql.orderBy = null;
		if(sql.groupBy != null) {
			// musime cely select obalit este do count query, lebo to robi count pre grupy!
			var subQuery = sql;
			sql = new Sql();
			sql.select("count(*)", subQuery);
		}
		return sql;
	}
}
