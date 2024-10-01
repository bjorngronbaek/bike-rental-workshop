package io.axoniq.demo.bikerental.rental.command;

import org.axonframework.spring.stereotype.Aggregate;
import io.axoniq.demo.bikerental.coreapi.rental.*;

@Aggregate
public class Bike {

    @AggregateIdentifier
    private String bikeId;

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    public void handle(RegisterBikeCommand command){
        AggregateLifecycle.apply(new BikeRegisteredEvent(
            command.bikeId(), command.bikeType(), command.location()));
    }
}
