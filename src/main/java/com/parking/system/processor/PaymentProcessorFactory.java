package com.parking.system.processor;

import com.parking.system.enums.PaymentMethod;
import com.parking.system.exception.BusinessException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessorFactory {

    private final Map<PaymentMethod, PaymentProcessor> processors = new EnumMap<>(PaymentMethod.class);

    public PaymentProcessorFactory(List<PaymentProcessor> processorList) {
        for (PaymentProcessor processor : processorList) {
            processors.put(processor.supports(), processor);
        }
    }

    public PaymentProcessor getProcessor(PaymentMethod method) {
        PaymentProcessor processor = processors.get(method);
        if (processor == null) {
            throw new BusinessException("Unsupported payment method: " + method);
        }
        return processor;
    }
}
