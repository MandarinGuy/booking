package org.mandarin.booking.domain.member;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter()
public class MemberAuthorityConverter implements AttributeConverter<List<MemberAuthority>, String> {

    private static final String DELIM = ",";

    @Override
    public String convertToDatabaseColumn(List<MemberAuthority> attribute) {
        if (attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(MemberAuthority::name)
                .collect(Collectors.joining(DELIM));
    }

    @Override
    public List<MemberAuthority> convertToEntityAttribute(String dbData) {
        if (dbData.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(dbData.split(DELIM))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(MemberAuthority::valueOf)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
