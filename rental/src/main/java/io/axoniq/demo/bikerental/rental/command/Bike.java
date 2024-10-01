package io.axoniq.demo.bikerental.rental.command;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;
import io.axoniq.demo.bikerental.coreapi.rental.*;

@Aggregate
public class Bike {

    @AggregateIdentifier
    private String bikeId;
    private String rentalReference;
    private String renter;

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    public void handle(RegisterBikeCommand command){
        AggregateLifecycle.apply(new BikeRegisteredEvent(
            command.bikeId(), command.bikeType(), command.location()));
    }

    @CommandHandler
    public String handle(RequestBikeCommand command){
        String rentalReference = UUID.randomUUID().toString();
        AggregateLifecycle.apply(new BikeRequestedEvent(
            command.bikeId(), command.renter(),rentalReference));
        return rentalReference;
    }

    @EventSourcingHandler
    public void handleBikeRegisteredEvent(BikeRegisteredEvent event){
        this.bikeId = event.bikeId();
    }

    @EventSourcingHandler
    public void handleBikeRequestedEvent(BikeRequestedEvent event){
        this.rentalReference = event.rentalReference();
        this.renter = event.renter();
    }



}
