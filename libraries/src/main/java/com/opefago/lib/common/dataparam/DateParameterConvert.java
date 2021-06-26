package com.opefago.lib.common.dataparam;

import com.opefago.lib.common.dataparam.annotations.DateFormat;
import com.opefago.lib.common.dataparam.annotations.DateTimeFormat;
import lombok.Setter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
public class DateParameterConvert  implements ParamConverter<Date> {
    public static final String DEFAULT_FORMAT = DateFormat.DEFAULT_DATE;

    private DateTimeFormat customDateTimeFormat;
    private DateFormat customDateFormat;

    @Override
    public Date fromString(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        String format = DEFAULT_FORMAT;
        if (customDateFormat != null) {
            format = customDateFormat.value();
        } else if (customDateTimeFormat != null) {
            format = customDateTimeFormat.value();
        }

        final SimpleDateFormat simpleDateFormat = new
                SimpleDateFormat(format);

        try {
            return simpleDateFormat.parse(string);
        } catch (ParseException ex) {
            throw new WebApplicationException(ex);
        }
    }

    @Override
    public String toString(Date date) {
        if(date != null)
        return new SimpleDateFormat(DEFAULT_FORMAT).format(date);
        return null;
    }
}
