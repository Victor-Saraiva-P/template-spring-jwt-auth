package com.victorsaraiva.auth_base_jwt.controller;

import com.victorsaraiva.auth_base_jwt.dtos.jwt.AccessTokenDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.ChangeRoleRequestDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UpdateAccountResponseDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UpdateUserDTO;
import com.victorsaraiva.auth_base_jwt.dtos.user.UserDTO;
import com.victorsaraiva.auth_base_jwt.exceptions.user.UnauthorizedAccountDeletionException;
import com.victorsaraiva.auth_base_jwt.models.UserEntity;
import com.victorsaraiva.auth_base_jwt.security.CustomUserDetails;
import com.victorsaraiva.auth_base_jwt.services.UserService;
import com.victorsaraiva.auth_base_jwt.services.security.AccessTokenService;
import com.victorsaraiva.auth_base_jwt.services.security.BlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.base-url}/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AccessTokenService accessTokenService;
  private final BlacklistService blacklistService;

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @PatchMapping("/change-role/{userId}")
  public ResponseEntity<Void> changeUserRole(
    @PathVariable UUID userId, @Valid @RequestBody ChangeRoleRequestDTO changeRoleRequest) {

    userService.changeUserRole(changeRoleRequest, userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete-your-account")
  public ResponseEntity<Void> deleteUser(
    @RequestHeader("Authorization") String authHeader,
    @AuthenticationPrincipal CustomUserDetails userDetails) {

    String accessToken = authHeader.replace("Bearer ", "");
    String userIdFromAccessToken = accessTokenService.extractSubject(accessToken);
    UserEntity loggedUser = userDetails.user();

    if (!userIdFromAccessToken.equals(loggedUser.getId().toString())) {
      throw new UnauthorizedAccountDeletionException();
    }

    userService.deleteUser(loggedUser.getId());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update-your-account")
  public ResponseEntity<UpdateAccountResponseDTO> updateUser(
    @RequestHeader("Authorization") String authHeader,
    @AuthenticationPrincipal CustomUserDetails userDetails,
    @Valid @RequestBody UpdateUserDTO updateUserDTO) {

    UserEntity loggedUser = userDetails.user();

    UserDTO updatedUser = userService.updateUser(updateUserDTO, loggedUser.getId());

    // Adiciona o access token à blacklist
    blacklistService.blacklistByAuthHeader(authHeader);

    // Gera um novo access token
    String newAccessToken = accessTokenService.generateToken(loggedUser);

    AccessTokenDTO accessTokenDTO = new AccessTokenDTO(newAccessToken);

    UpdateAccountResponseDTO response = new UpdateAccountResponseDTO(updatedUser, accessTokenDTO);
    return ResponseEntity.ok(response);
  }
}
