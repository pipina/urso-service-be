package sk.is.urso.config;

import org.alfa.utils.DateUtils;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sk.is.urso.subject.v1.PoboxT;
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
        modelMapper.addConverter(codelistItemToString);
        modelMapper.addConverter(poBoxToString);
        modelMapper.addConverter(localToXmlDate);
        modelMapper.addConverter(xmlToLocalDate);
        modelMapper.addConverter(dateToLocalDate);
        return modelMapper;
    }
}
