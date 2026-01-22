package com.epam.finaltask.service;

import java.util.List;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO order(String id, String userId);
    VoucherDTO update(String id, VoucherDTO voucherDTO);
    void delete(String voucherId);
    VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO);
    List<VoucherDTO> findAllByUserId(String userId);

    List<VoucherDTO> findAllByTourType(TourType tourType);
    List<VoucherDTO> findAllByTransferType(String transferType);
    List<VoucherDTO> findAllByPrice(Double price);
    List<VoucherDTO> findAllByHotelType(HotelType hotelType);
    List<VoucherDTO> findAllByStatus(String status);

    List<VoucherDTO> findAll();
    Page<Voucher> findAll(Specification<Voucher> spec, Pageable pageable);
}
