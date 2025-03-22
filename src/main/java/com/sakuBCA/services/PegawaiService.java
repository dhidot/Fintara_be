package com.sakuBCA.services;

import com.sakuBCA.models.PegawaiDetails;
import com.sakuBCA.repositories.PegawaiDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PegawaiService {
    private final PegawaiDetailsRepository pegawaiDetailsRepository;

    public PegawaiDetails savePegawaiDetails(PegawaiDetails pegawaiDetails) {
        return pegawaiDetailsRepository.save(pegawaiDetails);
    }
}
