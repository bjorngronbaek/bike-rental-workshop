package io.axoniq.demo.bikerental.rental.query;

import java.io.Console;
import java.util.List;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import io.axoniq.demo.bikerental.coreapi.rental.BikeInUseEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRequestedEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeStatus;

@Component
public class BikeStatusProjection {

    private final BikeStatusRepository bikeStatusRepository;

    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository) {
        this.bikeStatusRepository = bikeStatusRepository;
    }

    // TODO Implement relevant Query and Event Handlers

    @EventHandler
    public void handle(BikeRegisteredEvent event){
        System.out.println("Storing bike with id "+event.bikeId());
        bikeStatusRepository.save(new BikeStatus(event.bikeId(), event.bikeType(), event.location()));
    }

    @EventHandler
    public void handle(BikeRequestedEvent event){
        BikeStatus bs = bikeStatusRepository.findById(event.bikeId()).get();
        bs.requestedBy(event.renter());
        bikeStatusRepository.save(bs);
    }

    @EventHandler
    public void handle(BikeInUseEvent event){
        System.out.println("Updating projection with BikeInUse for "+event.bikeId());
        BikeStatus bs = bikeStatusRepository.findById(event.bikeId()).get();
        bs.rentedBy(event.renter());
        bikeStatusRepository.save(bs);
    }

    @QueryHandler(queryName = "findAll")
    public List<BikeStatus> handle(){
        return bikeStatusRepository.findAll();
    }
}
