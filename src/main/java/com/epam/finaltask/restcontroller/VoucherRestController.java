package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherRestController {

    private final VoucherService voucherService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        List<VoucherDTO> vouchers = voucherService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> findAllByUserId(@PathVariable String userId) {
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody VoucherDTO voucherDTO) {
        VoucherDTO created = voucherService.create(voucherDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher is successfully created");
        response.put("voucher", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String id, @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updated = voucherService.update(id, voucherDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher is successfully updated");
        response.put("voucher", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        voucherService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", String.format("Voucher with Id %s has been deleted", id));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Map<String, Object>> changeHotStatus(@PathVariable String id, @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updated = voucherService.changeHotStatus(id, voucherDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher status is successfully changed");
        response.put("voucher", updated);
        return ResponseEntity.ok(response);
    }
}
