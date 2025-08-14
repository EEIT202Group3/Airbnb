package com.EEITG3.Airbnb.carRent.service;

import com.EEITG3.Airbnb.carRent.entity.VehicleDamage;
import com.EEITG3.Airbnb.carRent.repository.VehicleDamageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class VehicleDamageServiceImpl implements VehicleDamageService {
    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private final VehicleDamageRepository vdRepository;

    public VehicleDamageServiceImpl(VehicleDamageRepository repo) {
        this.vdRepository = repo;
    }

    @Override
    public String saveToFrontPublic(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf(".") + 1).toLowerCase()
                : "";
        if (!ALLOWED.contains(ext)) {
            throw new IOException("僅允許上傳圖片檔");
        }

        String projectRoot = System.getProperty("user.dir");
        String uploadDir = projectRoot + "/../front-end/public/carPicture/carDamages/";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
        }

        String fileName = UUID.randomUUID() + "_" + (original == null ? ("image." + ext) : original);
        File dest = new File(dir, fileName);

        try (InputStream is = file.getInputStream(); OutputStream os = new FileOutputStream(dest)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) != -1) os.write(buf, 0, len);
        }
        return "/carPicture/carDamages/" + fileName;
    }

    @Override
    public void deletePhysicalByUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/carPicture/carDamages/")) return;
        String projectRoot = System.getProperty("user.dir");
        String abs = projectRoot + "/../front-end/public" + imageUrl;
        try {
            Files.deleteIfExists(new File(abs).toPath());
        } catch (Exception ignored) {
        }
    }

    @Override
    public List<VehicleDamage> listByReservation(Integer reservationId) {
        return vdRepository.findByReservationIdOrderByReportDateDesc(reservationId);
    }

    @Transactional
    @Override
    public List<VehicleDamage> create(Integer reservationId, String note, List<MultipartFile> images) throws IOException {
        List<VehicleDamage> result = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            VehicleDamage d = new VehicleDamage();
            d.setReservationId(reservationId);
            d.setDamageNote(note);
            d.setImageUrl(null);
            result.add(vdRepository.save(d));
            return result;
        }
        for (MultipartFile f : images) {
            String url = saveToFrontPublic(f);
            VehicleDamage d = new VehicleDamage();
            d.setReservationId(reservationId);
            d.setDamageNote(note);
            d.setImageUrl(url);
            result.add(vdRepository.save(d));
        }
        return result;
    }

    @Transactional
    @Override
    public VehicleDamage updateNote(Integer damageId, String note) {
        VehicleDamage d = vdRepository.findById(damageId)
                .orElseThrow(() -> new IllegalArgumentException("找不到損壞紀錄"));
        d.setDamageNote(note);
        return vdRepository.save(d);
    }

    @Transactional
    @Override
    public void delete(Integer damageId) {
        vdRepository.findById(damageId).ifPresent(d -> {
            deletePhysicalByUrl(d.getImageUrl());
            vdRepository.delete(d);
        });
    }
}
