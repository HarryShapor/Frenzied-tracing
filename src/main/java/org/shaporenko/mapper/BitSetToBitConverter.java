package org.shaporenko.mapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.BitSet;

@Converter(autoApply = true)
public class BitSetToBitConverter implements AttributeConverter<BitSet, String> {

    private static final int MAX_BITS = 25;

    @Override
    public String convertToDatabaseColumn(BitSet attribute) {
        if (attribute == null) {
            return null;
        }

        // PostgreSQL BIT тип ожидает строку вида '10101'
        StringBuilder sb = new StringBuilder();
        for (int i = MAX_BITS - 1; i >= 0; i--) {
            sb.append(attribute.get(i) ? '1' : '0');
        }
        return sb.toString();
    }

    @Override
    public BitSet convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new BitSet(MAX_BITS);
        }

        BitSet bitSet = new BitSet(MAX_BITS);
        for (int i = 0; i < dbData.length() && i < MAX_BITS; i++) {
            if (dbData.charAt(dbData.length() - 1 - i) == '1') {
                bitSet.set(i);
            }
        }
        return bitSet;
    }
}