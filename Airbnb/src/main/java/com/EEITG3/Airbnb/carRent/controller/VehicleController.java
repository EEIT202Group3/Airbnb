package com.EEITG3.Airbnb.carRent.controller;

import com.EEITG3.Airbnb.carRent.entity.Vehicle;
import com.EEITG3.Airbnb.carRent.repository.VehicleRepository;
import com.EEITG3.Airbnb.carRent.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    private final VehicleService vService;
    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleService vService, VehicleRepository vehicleRepository) {
        this.vService = vService;
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping("/insert")
    public Vehicle insert(@RequestBody Vehicle vehicle) {
        try {
            return vService.insert(vehicle);
        } catch (Exception e) {
            throw new RuntimeException("新增失敗" + e);
        }
    }

    @PutMapping("/update")
    public Vehicle update(@RequestBody Vehicle vehicle) {
        Vehicle original = vehicleRepository.findById(vehicle.getVehicleId())
                .orElseThrow(() -> new RuntimeException("找不到車輛"));
        original.setPlateNo(vehicle.getPlateNo());
        original.setBrand(vehicle.getBrand());
        original.setModel(vehicle.getModel());
        original.setColor(vehicle.getColor());
        original.setFuelType(vehicle.getFuelType());
        original.setTransmission(vehicle.getTransmission());
        original.setSeatCapacity(vehicle.getSeatCapacity());
        original.setFuelCapacity(vehicle.getFuelCapacity());
        original.setVehicleTax(vehicle.getVehicleTax());
        original.setDailyRent(vehicle.getDailyRent());
        original.setMileage(vehicle.getMileage());
        original.setLatitude(vehicle.getLatitude());
        original.setLongitude(vehicle.getLongitude());
        original.setStatus(vehicle.getStatus());
        original.setImage(vehicle.getImage());
        System.out.println("接收到的圖片檔名：" + vehicle.getImage());
        return vehicleRepository.save(original);
    }

    @DeleteMapping("/delete/{id}")
    public Integer delete(@PathVariable Integer id) throws Exception {
        if (id == null) {
            throw new Exception("刪除失敗");
        }
        return vService.deleteById(id);
    }

    @GetMapping("/findall")
    public List<Vehicle> findAll() {
        return vService.findAll();
    }

    @GetMapping("/plateno")
    public Vehicle findByDriverPhone(@RequestParam String plateNo) throws Exception {
        return vService.findByPlateNo(plateNo);
    }

    @GetMapping("/search")
    public Vehicle findByPlateNo(
            @RequestParam String plateNo) {
        try {
            return vService.findByPlateNo(plateNo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查詢錯誤：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Vehicle findById(@PathVariable Integer id) throws Exception {
        return vService.findById(id);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String projectRoot = System.getProperty("user.dir");  // Airbnb 專案根
            String uploadDir = projectRoot + "/../front-end/public/carPicture/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 建資料夾
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destFile = new File(uploadDir, fileName);

            try (InputStream is = file.getInputStream();
                 OutputStream os = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            Map<String, String> response = new HashMap<>();
            response.put("filename", fileName);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
