package com.callicoder.goparking.domain;

import com.callicoder.goparking.exceptions.ParkingLotFullException;
import com.callicoder.goparking.exceptions.SlotNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class ParkingLot {

    private final int numSlots;
    private final int numFloors;
    private SortedSet<ParkingSlot> availableSlots = new TreeSet<>();
    private Set<ParkingSlot> occupiedSlots = new HashSet<>();
    private Map<Integer, ParkingSlot> parkingSlotMap = new HashMap<>();
    private Set<String> carsParked = new HashSet<>();

    public ParkingLot(int numSlots) {
        if (numSlots <= 0) {
            throw new IllegalArgumentException(
                "Number of slots in the Parking Lot must be greater than zero."
            );
        }

        // Assuming Single floor since only numSlots are specified in the input.
        this.numSlots = numSlots;
        this.numFloors = 1;

        for (int i = 0; i < numSlots; i++) {
            ParkingSlot parkingSlot = new ParkingSlot(i + 1, 1);
            this.availableSlots.add(parkingSlot);
            this.parkingSlotMap.put(i + 1, parkingSlot);
        }
    }

    public synchronized Ticket reserveSlot(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car must not be null");
        }

        if (this.isFull()) {
            throw new ParkingLotFullException();
        }

        if (carsParked.contains(car.getRegistrationNumber())) return null;
        carsParked.add(car.getRegistrationNumber());

        ParkingSlot nearestSlot = this.availableSlots.first();

        nearestSlot.reserve(car);
        this.availableSlots.remove(nearestSlot);
        this.occupiedSlots.add(nearestSlot);

        return new Ticket(
            nearestSlot.getSlotNumber(),
            car.getRegistrationNumber(),
            car.getColor()
        );
    }

    public ParkingSlot leaveSlot(int slotNumber) {
        //TODO: implement leave
        if (slotNumber > numSlots) {
            throw new SlotNotFoundException(slotNumber);
        }


        ParkingSlot parkingSlot = parkingSlotMap.get(slotNumber);
        if (parkingSlot.getCar() != null) {
            this.occupiedSlots.remove(parkingSlot);
            this.availableSlots.add(parkingSlot);
            carsParked.remove(parkingSlot.getCar().getRegistrationNumber());
        }
        parkingSlot.clear();
        return parkingSlot;
//        return null;
    }

    public boolean isFull() {
        return this.availableSlots.isEmpty();
    }

    public List<String> getRegistrationNumbersByColor(String color) {
        //TODO: implement getRegistrationNumbersByColor
        List<String> regNos = new ArrayList<>();
        for (ParkingSlot parkingSlot: occupiedSlots) {
            if (parkingSlot.getCar().getColor().equals(color)) regNos.add(parkingSlot.getCar().getRegistrationNumber());
        }
        return regNos;
    }

    public List<Integer> getSlotNumbersByColor(String color) {
        //TODO: implement getSlotNumbersByColor
        List<Integer> slotNos = new ArrayList<>();
        for (ParkingSlot parkingSlot: occupiedSlots) {
            if (parkingSlot.getCar().getColor().equals(color)) slotNos.add(parkingSlot.getSlotNumber());
        }
        return slotNos;
    }

    public Optional<Integer> getSlotNumberByRegistrationNumber(
        String registrationNumber
    ) {
        //TODO: implement getSlotNumberByRegistrationNumber
        Integer slotNo = null;
        Optional<Integer> res = Optional.empty();

        for (ParkingSlot parkingSlot: occupiedSlots) {
            if (parkingSlot.getCar().getRegistrationNumber().equals(registrationNumber)) {
                slotNo = parkingSlot.getSlotNumber();
                res = Optional.of(slotNo);
                break;
            }
        }

        return res;
    }

    public int getNumSlots() {
        return numSlots;
    }

    public int getNumFloors() {
        return numFloors;
    }

    public SortedSet<ParkingSlot> getAvailableSlots() {
        return availableSlots;
    }

    public Set<ParkingSlot> getOccupiedSlots() {
        return occupiedSlots;
    }
}
