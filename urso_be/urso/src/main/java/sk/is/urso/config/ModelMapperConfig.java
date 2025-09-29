package sk.is.urso.config;

import org.alfa.utils.DateUtils;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sk.is.urso.rest.model.PolozkaCiselnika;
import sk.is.urso.rest.model.PolozkaCiselnikaVolitelna;
import sk.is.urso.subject.v1.PoboxT;
import sk.is.urso.subject.v1.REGCodelistItemOptionalT;
import sk.is.urso.subject.v1.REGCodelistItemT;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.Date;

@Component
public class ModelMapperConfig {

    @Bean(name = "getMapper")
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Converter<XMLGregorianCalendar, LocalDate> xmlToLocalDate = new AbstractConverter<>() {
            @Override
            protected LocalDate convert(XMLGregorianCalendar source) {
                return source == null ? null : DateUtils.toLocalDate(source);
            }
        };
        Converter<LocalDate, XMLGregorianCalendar> localToXmlDate = new AbstractConverter<>() {
            @Override
            protected XMLGregorianCalendar convert(LocalDate source) {
                return source == null ? null : DateUtils.toXmlDate(source);
            }
        };
        Converter<Date, LocalDate> dateToLocalDate = new AbstractConverter<>() {
            @Override
            protected LocalDate convert(Date source) {
                return source == null ? null : DateUtils.toLocalDate(source);
            }
        };
        Converter<PoboxT, String> poBoxToString = new AbstractConverter<>() {
            @Override
            protected String convert(PoboxT source) {
                return source == null ? null : source.getPoBox();
            }
        };
        Converter<REGCodelistItemT, String> codelistItemToString = new AbstractConverter<>() {
            @Override
            protected String convert(REGCodelistItemT source) {
                return source == null ? null : source.getCodeListCode();
            }
        };
        Converter<REGCodelistItemT, PolozkaCiselnika> toEnumerationItem = new AbstractConverter<>() {
            @Override
            protected PolozkaCiselnika convert(REGCodelistItemT source) {
                if (source != null) {
                    PolozkaCiselnika enumerationItem = new PolozkaCiselnika();
                    enumerationItem.setKodPolozky(source.getItemCode());
                    enumerationItem.setHodnotaPolozky(source.getItemValue());
                    enumerationItem.setKodCiselnika(source.getCodeListCode());
                    return enumerationItem;
                }
                return null;
            }
        };
        Converter<REGCodelistItemOptionalT, PolozkaCiselnikaVolitelna> toEnumerationItemOptional = new AbstractConverter<>() {
            @Override
            protected PolozkaCiselnikaVolitelna convert(REGCodelistItemOptionalT source) {
                if (source != null) {
                    PolozkaCiselnikaVolitelna enumerationItem = new PolozkaCiselnikaVolitelna();
                    enumerationItem.setKodPolozky(source.getItemCode());
                    enumerationItem.setHodnotaPolozky(source.getItemValue());
                    enumerationItem.setKodCiselnika(source.getCodeListCode());
                    return enumerationItem;
                }
                return null;
            }
        };
        Converter<PolozkaCiselnikaVolitelna, REGCodelistItemOptionalT> toREGCodelistItemOptional = new AbstractConverter<>() {
            @Override
            protected REGCodelistItemOptionalT convert(PolozkaCiselnikaVolitelna source) {
                if (source != null) {
                    REGCodelistItemOptionalT codelistItemOptional = new REGCodelistItemOptionalT();
                    if (source.getKodPolozky() != null) {
                        codelistItemOptional.setItemCode(source.getKodPolozky());
                    }
                    else {
                        codelistItemOptional.setItemValue(source.getHodnotaPolozky());
                    }
                    codelistItemOptional.setCodeListCode(source.getKodCiselnika());
                    return codelistItemOptional;
                }
                return null;
            }
        };

        Converter<PolozkaCiselnika, REGCodelistItemT> toREGCodelistItem = new AbstractConverter<>() {
            @Override
            protected REGCodelistItemT convert(PolozkaCiselnika source) {
                if (source != null) {
                    REGCodelistItemT codelistItem = new REGCodelistItemT();
                    codelistItem.setItemCode(source.getKodPolozky());
                    codelistItem.setCodeListCode(source.getKodCiselnika());
                    return codelistItem;
                }
                return null;
            }
        };

        modelMapper.addConverter(toREGCodelistItem);
        modelMapper.addConverter(toREGCodelistItemOptional);
        modelMapper.addConverter(toEnumerationItemOptional);
        modelMapper.addConverter(toEnumerationItem);
        modelMapper.addConverter(codelistItemToString);
        modelMapper.addConverter(poBoxToString);
        modelMapper.addConverter(localToXmlDate);
        modelMapper.addConverter(xmlToLocalDate);
        modelMapper.addConverter(dateToLocalDate);
        return modelMapper;
    }
}
