package com.example.demo.sales.domain.model.factory;



import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;


@Component
public class SalesIdentifierFactory{
//    public static String nextPOID() {
//        return UUID.randomUUID().toString();
//    }

    /**
     * Gnereate unique ID from UUID in positive space
     * @return long value representing UUID
     */
    public static Long nextPOID()
    {
        long val = -1;
        do
        {
            final UUID uid = UUID.randomUUID();
            final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
            buffer.putLong(uid.getLeastSignificantBits());
            buffer.putLong(uid.getMostSignificantBits());
            final BigInteger bi = new BigInteger(buffer.array());
            val = bi.longValue();
        } while (val < 0);
        return val;
    }
}
