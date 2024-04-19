package com.nttdata.user.api.controller;

import com.nttdata.user.api.data.dto.UserDto;
import com.nttdata.user.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(properties = {
    "amazon.dynamodb.endpoint=http://localhost:8000/",
    "amazon.aws.accesskey=test1",
    "amazon.aws.secretkey=test231" })
public class UserControllerTest {

    @Mock
    private UserService userServiceMock;

    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userServiceMock);
    }

    @Test
    @DisplayName("Registrar Usuário com Sucesso")
    public void testRegisterUser_Success() {
        UserDto user = new UserDto();
        when(userServiceMock.existsByCpf(anyString())).thenReturn(false);
        when(userServiceMock.existsByEmail(anyString())).thenReturn(false);
        when(userServiceMock.registerUser(any(UserDto.class))).thenReturn(user);
        ResponseEntity<?> response = userController.registerUser(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

//    @Test
//    @DisplayName("Registrar Usuário com CPF Duplicado")
//    public void testRegisterUser_DuplicateCPF() {
//        UserDto user = new UserDto();
//        user.setCpf("12345678900");
//        when(userServiceMock.existsByCpf(anyString())).thenReturn(true);
//        ResponseEntity<?> response = userController.registerUser(user);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("CPF já está em uso", response.getBody());
//    }
//
//    @Test
//    @DisplayName("Registrar Usuário com E-mail Duplicado")
//    public void testRegisterUser_DuplicateEmail() {
//        UserDto user = new UserDto();
//        user.setEmail("test@example.com");
//        when(userServiceMock.existsByEmail(anyString())).thenReturn(true);
//        ResponseEntity<?> response = userController.registerUser(user);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("E-mail já está em uso", response.getBody());
//    }

    @Test
    @DisplayName("Registrar Usuário - Erro Interno no Servidor")
    public void testRegisterUser_InternalServerError() {
        UserDto user = new UserDto();
        when(userServiceMock.existsByCpf(anyString())).thenReturn(false);
        when(userServiceMock.existsByEmail(anyString())).thenReturn(false);
        when(userServiceMock.registerUser(user)).thenThrow(new RuntimeException("Erro no servidor"));
        ResponseEntity<?> response = userController.registerUser(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro ao processar o registro do usuário", response.getBody());
    }

    @Test
    @DisplayName("Login de Usuário com Sucesso")
    public void testLoginUser_Success() {
        UserDto loginRequest = new UserDto();
        loginRequest.setEmail("test@example.com");
        when(userServiceMock.findByEmail(any(UserDto.class))).thenReturn(true);
        ResponseEntity<Boolean> response = userController.loginUser(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @DisplayName("Login de Usuário Falhou")
    public void testLoginUser_Failure() {
        UserDto loginRequest = new UserDto();
        loginRequest.setEmail("test@example.com");
        when(userServiceMock.findByEmail(any(UserDto.class))).thenReturn(false);
        ResponseEntity<Boolean> response = userController.loginUser(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Login de Usuário - Erro Interno no Servidor")
    public void testLoginUser_Exception() {
        UserDto loginRequest = new UserDto();
        loginRequest.setEmail("test@example.com");
        when(userServiceMock.findByEmail(any(UserDto.class))).thenThrow(new RuntimeException("Erro ao processar o login"));
        ResponseEntity<Boolean> response = userController.loginUser(loginRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody());
    }

}
