package io.axoniq.demo.bikerental.rental.command;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Aggregate;
import io.axoniq.demo.bikerental.coreapi.rental.*;

@Aggregate
public class Bike {

    @AggregateIdentifier
    private String bikeId;
    private String renter;
    private String location;

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    public void handle(RegisterBikeCommand command){
        AggregateLifecycle.apply(new BikeRegisteredEvent(
            command.bikeId(), command.bikeType(), command.location()));
    }

    @CommandHandler
    public String handle(RequestBikeCommand command){
        System.out.println("Handling RequestBikeCommand for "+command.bikeId());
        if(renter != null){
            throw new IllegalStateException("Bike is already rented");
        }

        String rentalReference = UUID.randomUUID().toString();
        AggregateLifecycle.apply(new BikeRequestedEvent(
            command.bikeId(), command.renter(),rentalReference));
        return rentalReference;
    }

    @CommandHandler
    public void handle(ApproveRequestCommand command){
        System.out.println("Handlign ApproveRequest for "+command.renter());
        if(this.renter.equals(command.renter())){
            AggregateLifecycle.apply(new BikeInUseEvent(command.bikeId(), command.renter()));
        }
        else{
            throw new IllegalStateException(command.renter()+" is not the registered renter "+renter);
        }
    }

    @CommandHandler
    public void handle(RejectRequestCommand command){
        System.out.println("Handlign RejectRequest for "+command.renter());
        if(this.renter.equals(command.renter())){
            AggregateLifecycle.apply(new RequestRejectedEvent(command.bikeId()));
        }
    }

    @CommandHandler
    public void handle(ReturnBikeCommand command){
        System.out.println("Handling ReturnBike for "+command.bikeId());
        if(this.renter != null){
            AggregateLifecycle.apply(new BikeReturnedEvent(command.bikeId(), command.location()));
        }
    }

    @EventSourcingHandler
    public void handleBikeRegisteredEvent(BikeRegisteredEvent event){
        this.bikeId = event.bikeId();
        this.location = event.location();
    }

    @EventSourcingHandler
    public void handleBikeReturnedEvent(BikeReturnedEvent event){
        this.location = event.location();
        this.renter = null;
    }

    @EventSourcingHandler
    public void handleBikeRequestedEvent(BikeRequestedEvent event){
        this.renter = event.renter();
    }

    @EventSourcingHandler
    public void handleBikeRejectedEvent(RequestRejectedEvent event){
        this.renter = null;
    }
}
