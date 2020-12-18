package com.phonecompany.billing;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

	private long MINUTE5 = 60*5;

	@Override
	public BigDecimal calculate(String phoneLog) {
		List<String> rows = Stream.of(phoneLog.split("\n", -1))
				.collect(Collectors.toList());

		Function<String, Call> mapRow = (row) -> {
			String[] items = row.split(",");
			if (items.length == 3) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
				Call call = new Call(items[0],
						LocalDateTime.parse(items[1], formatter),
						LocalDateTime.parse(items[2], formatter));

				return call;
			}

			return null;
		};

		List<Call> calls = new ArrayList<>();

		for (String row : rows) {
			Call call = mapRow.apply(row);
			if (call != null) {
				calls.add(call);
			}
		}

		Map<String, Long> counters = calls.stream()
				.collect(Collectors.groupingBy(c -> c.getNumber(),
						Collectors.counting()));
		String maxCountNumber = Collections.max(counters.entrySet(), Comparator.comparing(Map.Entry::getValue)).getKey();

		BigDecimal price = BigDecimal.ZERO;

		for (Call call : calls) {
			if (call.getNumber().equals(maxCountNumber)) {
				continue;
			}

			long duration = Duration.between(call.getCallStart(), call.getCallStop()).getSeconds();
			boolean isFP = isFullPrice(call.getCallStart());
			boolean isLonger = duration > MINUTE5;

			if (isLonger) {
				duration -= MINUTE5;
			}
			long minutes = (duration + 60 - 1) / 60;

			if (isFP) {
				if (isLonger) {
					price = price.add(BigDecimal.valueOf(5));
					price = price.add(BigDecimal.valueOf(minutes * 0.2));
				} else {
					price = price.add(BigDecimal.valueOf(minutes * 1));
				}
			} else {
				if (isLonger) {
					price = price.add(BigDecimal.valueOf(2.5));
					price = price.add(BigDecimal.valueOf(minutes * 0.2));
				} else {
					price = price.add(BigDecimal.valueOf(minutes * 0.5));
				}
			}
		}

		return price;
	}

	private boolean isFullPrice(LocalDateTime ldt) {
		return ldt.getHour() >= 8 && ldt.getHour() < 16;
	}
}
