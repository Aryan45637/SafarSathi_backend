package com.seamlesstrackers.safarsathi.Controller;

import com.seamlesstrackers.safarsathi.entity.Rahasathi;
import com.seamlesstrackers.safarsathi.repository.Rahasathirepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class mycontroller {

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }


    @Autowired
    private Rahasathirepo rahasathirepo;

    // ✅ Login API (Only checks username, password is always "1111")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Rahasathi loginDetails) {
        Rahasathi bus = rahasathirepo.findByBusNo(loginDetails.getBusNo());

        if (bus == null) {
            return ResponseEntity.status(404).body("{\"error\": \"Invalid bus number\"}");
        }

        if (!"1111".equals(loginDetails.getUserid())) {
            return ResponseEntity.status(401).body("{\"error\": \"Incorrect password\"}");
        }

        return ResponseEntity.ok("{\"message\": \"Login successful\"}");
    }
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyBuses(@RequestParam double latitude, @RequestParam double longitude, @RequestParam(defaultValue = "30") double radius) {
        List<Rahasathi> allBuses = rahasathirepo.findAll();
        List<Rahasathi> nearbyBuses = allBuses.stream()
                .filter(bus -> haversineDistance(latitude, longitude, bus.getLatitude(), bus.getLongitude()) <= radius)
                .collect(Collectors.toList());

        return ResponseEntity.ok(nearbyBuses);
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(rahasathirepo.findAll());
    }

    // ✅ Update Bus Location
    @PutMapping("/update-location")
    public ResponseEntity<?> updateBusLocation(@RequestBody Map<String, Object> request) {
        try {
            String busNo = (String) request.get("busNumber");
            Double latitude = Double.valueOf(request.get("latitude").toString());
            Double longitude = Double.valueOf(request.get("longitude").toString());

            Rahasathi bus = rahasathirepo.findByBusNo(busNo);
            if (bus == null) {
                return ResponseEntity.status(404).body("{\"error\": \"Bus not found\"}");
            }

            // Update only if location changed
            if (!bus.getLatitude().equals(latitude) || !bus.getLongitude().equals(longitude)) {
                bus.setLatitude(latitude);
                bus.setLongitude(longitude);
                rahasathirepo.save(bus);

                System.out.println("Bus " + busNo + " location updated: (" + latitude + ", " + longitude + ")");
            }

            return ResponseEntity.ok(bus);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\": \"Invalid request data\"}");
        }
    }

    // ✅ Get Bus Location by Bus Number
    @GetMapping("/bus/{busNo}")
    public ResponseEntity<?> getBusLocation(@PathVariable String busNo) {
        Rahasathi bus = rahasathirepo.findByBusNo(busNo);
        return (bus != null)
                ? ResponseEntity.ok(bus)
                : ResponseEntity.status(404).body("{\"error\": \"Bus not found\"}");
    }

    @PutMapping("/collect-ticket")
    public ResponseEntity<?> collectTicket(@RequestBody Map<String, Object> request) {
        try {
            String busNo = (String) request.get("busNo");
            String destination = (String) request.get("location");
            int tickets = (int) request.get("tickets");
            int children = (int) request.getOrDefault("children", 0); // ✅ Include child tickets
            int totalTickets = tickets + children; // ✅ Count all as full tickets

            Rahasathi bus = rahasathirepo.findByBusNo(busNo);
            if (bus == null) {
                return ResponseEntity.status(404).body("{\"error\": \"Bus not found\"}");
            }

            // Store ticket data based on destination
            Map<String, Integer> ticketData = bus.getTicketsCollected();
            ticketData.put(destination, ticketData.getOrDefault(destination, 0) + totalTickets);
            bus.setAvailableSeats(bus.getAvailableSeats() - totalTickets); // ✅ Reduce total seats

            // If bus reaches the destination, restore seats
            if (bus.getLatitude() != null && bus.getLongitude() != null) {
                if (bus.getLatitude().equals(request.get("latitude")) && bus.getLongitude().equals(request.get("longitude"))) {
                    bus.setAvailableSeats(bus.getAvailableSeats() + ticketData.getOrDefault(destination, 0)); // ✅ Restore seats
                    ticketData.remove(destination); // ✅ Remove destination entry
                }
            }

            rahasathirepo.save(bus);
            return ResponseEntity.ok("{\"message\": \"Ticket collected successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\": \"Invalid request data\"}");
        }
    }



    // ✅ Increase seats when reaching the destination
    @PutMapping("/update-seats")
    public ResponseEntity<?> updateSeats(@RequestBody Map<String, Object> request) {
        try {
            String busNo = (String) request.get("busNo");

            Rahasathi bus = rahasathirepo.findByBusNo(busNo);
            if (bus == null) {
                return ResponseEntity.status(404).body("{\"error\": \"Bus not found\"}");
            }

            // Increase available seats
            bus.setAvailableSeats(bus.getAvailableSeats() + 1);
            rahasathirepo.save(bus);
            return ResponseEntity.ok("{\"message\": \"Seat count updated successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\": \"Invalid request data\"}");
        }
    }
}