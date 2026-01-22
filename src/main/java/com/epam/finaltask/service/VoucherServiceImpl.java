package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.CustomEntityNotFoundException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final UserRepository userRepository;

    private Voucher findVoucherById(String id) {
        return voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new CustomEntityNotFoundException(String.format("Voucher with Id %s not found", id),
                        StatusCodes.ENTITY_NOT_FOUND.name()));
    }

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        Voucher savedVoucher = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        log.info("Voucher with id {} ordered", id);
        Voucher voucher = findVoucherById(id);

        if (voucher.getStatus() != VoucherStatus.UNREGISTERED)
            throw new CustomEntityNotFoundException("No voucher with id " + id + " UNREGISTERED",
                    StatusCodes.ENTITY_NOT_FOUND.name());

        UUID uuid = UUID.fromString(userId);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new CustomEntityNotFoundException(
                        "User not found with id " + userId,
                        StatusCodes.ENTITY_NOT_FOUND.name()
                ));

        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.REGISTERED);
        Voucher savedVoucher = voucherRepository.save(voucher);

        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher voucher = findVoucherById(id);
        Voucher updatedVoucher = voucherMapper.toVoucher(voucherDTO);

        voucher.setTitle(updatedVoucher.getTitle());
        voucher.setDescription(updatedVoucher.getDescription());
        voucher.setPrice(updatedVoucher.getPrice());
        voucher.setTourType(updatedVoucher.getTourType());
        voucher.setTransferType(updatedVoucher.getTransferType());
        voucher.setHotelType(updatedVoucher.getHotelType());
        voucher.setStatus(updatedVoucher.getStatus());
        voucher.setArrivalDate(updatedVoucher.getArrivalDate());
        voucher.setEvictionDate(updatedVoucher.getEvictionDate());
        voucher.setUser(updatedVoucher.getUser());
        voucher.setHot(updatedVoucher.isHot());

        Voucher savedVoucher = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    public void delete(String voucherId) {
        UUID id = UUID.fromString(voucherId);
        findVoucherById(voucherId);

        // Delete by UUID
        voucherRepository.deleteById(id);
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher voucher = findVoucherById(id);
        voucher.setHot(voucherDTO.getIsHot());

        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        return voucherRepository.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        return voucherRepository.findAllByTourType(tourType)
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        return voucherRepository.findAllByTransferType(TransferType.valueOf(transferType))
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        return voucherRepository.findAllByPrice(price)
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByStatus(String status) {
        return processAndSortVouchers(voucherRepository.findAllByStatus(VoucherStatus.valueOf(status)));
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        return voucherRepository.findAllByHotelType(hotelType)
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAll() {
        return processAndSortVouchers(voucherRepository.findAll());
    }

    @Override
    public Page<Voucher> findAll(Specification<Voucher> spec, Pageable pageable) {
        return voucherRepository.findAll(spec, pageable);
    }

    private List<VoucherDTO> processAndSortVouchers(List<Voucher> vouchers) {
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .sorted(Comparator.comparing(VoucherDTO::getIsHot).reversed())
                .toList();
    }
}
