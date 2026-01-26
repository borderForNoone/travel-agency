package com.epam.finaltask.controller.rest;


import com.epam.finaltask.dto.PaginatedResponse;
import com.epam.finaltask.dto.RemoteResponse;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherRequest;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.service.VoucherService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse> createVoucher(@RequestBody VoucherDTO vDto) {
        VoucherDTO createdVDto = voucherService.create(vDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "Voucher is successfully created",
                        List.of(createdVDto)
                )
        );
    }

    @PatchMapping("/{voucherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse> updateVoucher(
            @PathVariable String voucherId,
            @RequestBody VoucherDTO vDto
    ) {
        VoucherDTO updatedVDto = voucherService.update(voucherId, vDto);

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "Voucher is successfully updated",
                        List.of(updatedVDto)
                )
        );
    }

    @DeleteMapping("/{voucherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse> deleteVoucher(@PathVariable String voucherId) {
        voucherService.delete(voucherId);

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        String.format("Voucher with Id %s has been deleted", voucherId),
                        null
                )
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse> getAllVouchersByStatus(@PathVariable String status) {
        List<VoucherDTO> voucherList = voucherService.findAllByStatus(status);

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "voucherList is successfully obtained",
                        voucherList
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<RemoteResponse> getAllVouchers() {
        List<VoucherDTO> voucherDTOList = voucherService.findAll();

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "voucherList is successfully obtained",
                        voucherDTOList
                )
        );
    }

    @PatchMapping("/{voucherId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<RemoteResponse> changeVoucherStatus(
            @PathVariable String voucherId,
            @RequestBody VoucherRequest voucherRequest
    ) {
        VoucherDTO voucherDTO = voucherService.changeStatus(
                voucherId,
                String.valueOf(voucherRequest.getAdditionalDetails().get(0))
        );

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "Voucher status is successfully changed",
                        List.of(voucherDTO)
                )
        );
    }

    @PatchMapping("/{voucherId}/hot")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<RemoteResponse> changeVoucherHotStatus(
            @PathVariable String voucherId,
            @RequestBody VoucherRequest voucherRequest
    ) {
        VoucherDTO dto = new VoucherDTO();
        dto.setIsHot(Boolean.parseBoolean(
                String.valueOf(voucherRequest.getAdditionalDetails().get(0))
        ));

        VoucherDTO updatedVoucher = voucherService.changeHotStatus(voucherId, dto);

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "Voucher hot status is successfully changed",
                        List.of(updatedVoucher)
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<RemoteResponse> getVouchers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy)
        );

        Specification<Voucher> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), VoucherStatus.valueOf(status)));
            }

            if (search != null && !search.trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("title")),
                        "%" + search.toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Voucher> voucherPage = voucherService.findAll(spec, pageable);

        PaginatedResponse<Voucher> response = new PaginatedResponse<>(
                voucherPage.getContent(),
                voucherPage.getNumber(),
                voucherPage.getSize(),
                voucherPage.getTotalElements(),
                voucherPage.getTotalPages()
        );

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "voucherList is successfully obtained",
                        List.of(response)
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RemoteResponse> getUserVouchers(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "arrivalDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy)
        );

        Specification<Voucher> spec = (root, query, cb) ->
                cb.equal(root.get("user").get("id"), UUID.fromString(userId));

        Page<Voucher> voucherPage = voucherService.findAll(spec, pageable);

        PaginatedResponse<Voucher> response = new PaginatedResponse<>(
                voucherPage.getContent(),
                voucherPage.getNumber(),
                voucherPage.getSize(),
                voucherPage.getTotalElements(),
                voucherPage.getTotalPages()
        );

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "voucherList is successfully obtained",
                        List.of(response)
                )
        );
    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RemoteResponse> orderVoucher(
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestBody VoucherRequest voucherOrderRequest
    ) {
        VoucherDTO voucherDTO = voucherService.order(
                voucherOrderRequest.getVoucherId(),
                username
        );

        return ResponseEntity.ok(
                RemoteResponse.create(
                        true,
                        StatusCodes.OK.name(),
                        "voucher was successfully ordered",
                        List.of(voucherDTO)
                )
        );
    }
}
