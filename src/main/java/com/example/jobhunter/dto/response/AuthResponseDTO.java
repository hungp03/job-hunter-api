package com.example.jobhunter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.example.jobhunter.dto.response.user.ResLoginDTO;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO {
    private ResLoginDTO resLoginDTO;
    private String refreshToken;
    private String deviceKey;

    public AuthResponseDTO(ResLoginDTO resLoginDTO, String refreshToken){
        this.resLoginDTO = resLoginDTO;
        this.refreshToken = refreshToken;
    }
}
