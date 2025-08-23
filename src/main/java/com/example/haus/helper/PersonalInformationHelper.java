package com.example.haus.helper;

import com.example.haus.domain.request.user.profile.UpdatePersonalInformationRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PersonalInformationHelper {

    public UpdatePersonalInformationRequestDto handleEmptyStrings(
            UpdatePersonalInformationRequestDto personalInformation) {
        if (personalInformation == null) {
            return null;
        }

        if (personalInformation.getFirstName() != null && personalInformation.getFirstName().trim().isEmpty()) {
            personalInformation.setFirstName(null);
        }

        if (personalInformation.getLastName() != null && personalInformation.getLastName().trim().isEmpty()) {
            personalInformation.setLastName(null);
        }

        if (personalInformation.getPhone() != null && personalInformation.getPhone().trim().isEmpty()) {
            personalInformation.setPhone(null);
        }

        if (personalInformation.getNationality() != null && personalInformation.getNationality().trim().isEmpty()) {
            personalInformation.setNationality(null);
        }

        if (personalInformation.getUpdateAddressRequestDto() != null) {
            if (personalInformation.getUpdateAddressRequestDto().getCountry() != null &&
                    personalInformation.getUpdateAddressRequestDto().getCountry().trim().isEmpty()) {
                personalInformation.getUpdateAddressRequestDto().setCountry(null);
            }

            if (personalInformation.getUpdateAddressRequestDto().getCity() != null &&
                    personalInformation.getUpdateAddressRequestDto().getCity().trim().isEmpty()) {
                personalInformation.getUpdateAddressRequestDto().setCity(null);
            }

            if (personalInformation.getUpdateAddressRequestDto().getDistrict() == null &&
                    personalInformation.getUpdateAddressRequestDto().getDistrict() == null) {
                personalInformation.getUpdateAddressRequestDto().setDistrict(null);
            }
            if (personalInformation.getUpdateAddressRequestDto().getCommune() == null &&
                    personalInformation.getUpdateAddressRequestDto().getCommune() == null) {
                personalInformation.getUpdateAddressRequestDto().setCommune(null);
            }
            if (personalInformation.getUpdateAddressRequestDto().getDetailAddress() == null &&
                    personalInformation.getUpdateAddressRequestDto().getDetailAddress() == null) {
                personalInformation.getUpdateAddressRequestDto().setDetailAddress(null);
            }
            personalInformation.setUpdateAddressRequestDto(null);
        }

        return personalInformation;
    }

}
