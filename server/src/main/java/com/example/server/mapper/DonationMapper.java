package com.example.server.mapper;

import com.example.server.dto.DonationDTO;
import com.example.server.entity.Donation;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface DonationMapper {
    DonationDTO toDonationDTO (Donation donation);
}
