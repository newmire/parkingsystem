package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.commons.math3.util.Precision;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class FareCalculatorService {

    private LocalDateTime convert(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private double round(double x) {
        return Precision.round(x, 2);

    }

    public void calculateFare(Ticket ticket, boolean regularUser) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        LocalDateTime inHour = convert(ticket.getInTime());
        LocalDateTime outHour = convert(ticket.getOutTime());

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long duration = ChronoUnit.MINUTES.between(inHour, outHour) - 30;
        duration = duration < 0 ? 0 : duration;
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (regularUser) {
                    ticket.setPrice(round(duration * (Fare.CAR_RATE_PER_HOUR / 60) * 0.95));

                } else
                    ticket.setPrice(round(duration * (Fare.CAR_RATE_PER_HOUR / 60)));
            }
            break;

            case BIKE: {
                if (regularUser) {
                    ticket.setPrice(round(duration * (Fare.BIKE_RATE_PER_HOUR / 60) * 0.95));
                } else
                    ticket.setPrice(round(duration * (Fare.BIKE_RATE_PER_HOUR / 60)));
            }
            break;

            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}