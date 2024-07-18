package ru.olbreslavets.tgbank.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.olbreslavets.tgbank.dto.PaymentDto;
import ru.olbreslavets.tgbank.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "from", source = "source.payer.number")
    @Mapping(target = "to", source = "source.payee.number")
    PaymentDto toDto(Payment source, Long userId);

    @AfterMapping
    default void afterMapping(@MappingTarget PaymentDto target, Payment source, Long userId) {
        if (source != null) {
            if (source.getPayer() != null && source.getPayer().getOwner() != null && userId.equals(source.getPayer().getOwner().getTgId())) {
                var amount = "-" + source.getAmount();
                target.setAmount(amount);
            }
            if (source.getPayee() != null && source.getPayee().getOwner() != null && userId.equals(source.getPayee().getOwner().getTgId())) {
                var amount = "+" + source.getAmount();
                target.setAmount(amount);
            }
        }
    }

}
