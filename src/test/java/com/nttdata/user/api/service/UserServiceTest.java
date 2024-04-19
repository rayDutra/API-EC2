package com.nttdata.user.api.service;

import com.nttdata.user.api.data.dto.UserDto;
import com.nttdata.user.api.data.entity.UserEntity;
import com.nttdata.user.api.data.mapper.UserMapper;
import com.nttdata.user.api.exception.UserRegistrationException;
import com.nttdata.user.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserService.class)
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = {
    "amazon.dynamodb.endpoint=http://localhost:8000/",
    "amazon.aws.accesskey=test1",
    "amazon.aws.secretkey=test231" })
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Registrar novo usuário com sucesso")
    public void testRegisterUser_Success() {
        UserDto userDto = new UserDto(null, "John", "59632418042", "john@example.com", "password", null);
        UserEntity userEntity = new UserEntity(null, "John", "59632418042", "john@example.com", "encodedPassword", null);
        when(userRepository.existsByCpf(userDto.getCpf())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(passwordEncoder.encode(userDto.getSenha())).thenReturn("encodedPassword");
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);
        UserDto result = userService.registerUser(userDto);
        assertNotNull(result);
        assertEquals("John", result.getNome());
        verify(userRepository, times(1)).existsByCpf(userDto.getCpf());
        verify(userMapper, times(1)).toEntity(userDto);
        when(passwordEncoder.encode(userDto.getSenha())).thenReturn("encodedPassword");
        verify(userRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).toDto(userEntity);
    }

    @Test
    @DisplayName("Registrar usuário com CPF duplicado")
    public void testRegisterUser_DuplicateCPF() {
        UserDto userDto = new UserDto(null, "John", "59632418042", "john@example.com", "password", null);
        when(userRepository.existsByCpf(userDto.getCpf())).thenReturn(true);
        assertThrows(UserRegistrationException.class, () -> {
            userService.registerUser(userDto);
        });
        verify(userRepository, times(1)).existsByCpf(userDto.getCpf());
        verify(userMapper, never()).toEntity(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }
    @Test
    @DisplayName("Buscar usuário por e-mail existente com senha correta")
    public void testFindByEmail_ValidUserAndPassword() {
        UserDto loginRequest = new UserDto(null, null, null, "john@example.com", "password", null);
        UserEntity userEntity = new UserEntity(null, "John", "59632418042", "john@example.com", "encodedPassword", null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequest.getSenha(), userEntity.getSenha())).thenReturn(true);
        boolean result = userService.findByEmail(loginRequest);
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getSenha(), userEntity.getSenha());
    }

    @Test
    @DisplayName("Buscar usuário por e-mail inexistente")
    public void testFindByEmail_NonExistingUser() {
        UserDto loginRequest = new UserDto(null, null, null, "unknown@example.com", "password", null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        boolean result = userService.findByEmail(loginRequest);
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
    }
    @Test
    @DisplayName("Verificar existência de usuário por e-mail")
    public void testExistsByEmail() {
        String existingEmail = "john@example.com";
        UserEntity userEntity = new UserEntity(null, "John", "59632418042", "john@example.com", "encodedPassword", null);
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(userEntity));

        boolean result = userService.existsByEmail(existingEmail);

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(existingEmail);
    }

    @Test
    @DisplayName("Verificar inexistência de usuário por e-mail")
    public void testNotExistsByEmail() {
        String nonExistingEmail = "unknown@example.com";
        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());
        boolean result = userService.existsByEmail(nonExistingEmail);
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(nonExistingEmail);
    }
    @Test
    @DisplayName("Buscar usuário por e-mail e senha válidos")
    public void testFindByEmailAndPassword_Valid() {
        String email = "john@example.com";
        String senha = "password";
        UserEntity userEntity = new UserEntity(null, "John", "59632418042", "john@example.com", "encodedPassword", null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(senha, userEntity.getSenha())).thenReturn(true);
        when(userMapper.toDto(userEntity)).thenReturn(new UserDto(null, "John", "59632418042", "john@example.com", null, null));
        UserDto result = userService.findByEmailAndPassword(email, senha);
        assertNotNull(result);
        assertEquals("John", result.getNome()); // Verifica se o nome do usuário está correto
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(senha, userEntity.getSenha());
        verify(userMapper, times(1)).toDto(userEntity);
    }

    @Test
    @DisplayName("Buscar usuário por e-mail e senha inválidos")
    public void testFindByEmailAndPassword_Invalid() {
        String email = "john@example.com";
        String senha = "invalidPassword";
        UserEntity userEntity = new UserEntity(null, "John", "59632418042", "john@example.com", "encodedPassword", null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(senha, userEntity.getSenha())).thenReturn(false);
        UserDto result = userService.findByEmailAndPassword(email, senha);
        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(senha, userEntity.getSenha());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Buscar usuário por e-mail inexistente")
    public void testFindByEmailAndPassword_NonExistingEmail() {
        String email = "unknown@example.com";
        String senha = "password";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        UserDto result = userService.findByEmailAndPassword(email, senha);
        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userMapper, never()).toDto(any());
    }
    @Test
    @DisplayName("Registrar usuário com CPF inválido")
    public void testRegisterUser_InvalidCPF() {
        UserDto userDto = new UserDto(null, "John", "12345678900", "john@example.com", "password", null);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> {
            userService.registerUser(userDto);
        });
        assertEquals("Erro ao registrar usuário: CPF inválido", exception.getMessage());
        verify(userRepository, never()).existsByCpf(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
    @Test
    @DisplayName("Verificar tratamento de exceção ao verificar CPF")
    public void testExistsByCpf_ExceptionHandling() {
        String cpfWithError = "98765432100";
        when(userRepository.existsByCpf(cpfWithError)).thenThrow(new RuntimeException("Erro ao verificar CPF"));
        UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> {
            userService.existsByCpf(cpfWithError);
        });
        assertEquals("Erro ao verificar CPF: Erro ao verificar CPF", exception.getMessage());
        verify(userRepository, times(1)).existsByCpf(cpfWithError);
    }
    @Test
    @DisplayName("Buscar usuário por e-mail existente com senha incorreta")
    public void testFindByEmail_ValidUserAndWrongPassword() {
        UserDto loginRequest = new UserDto(null, null, null, "john@example.com", "password", null);
        UserEntity userEntity = new UserEntity(null, "John", "59632418042", "john@example.com", "encodedPassword", null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequest.getSenha(), userEntity.getSenha())).thenReturn(false);
        boolean result = userService.findByEmail(loginRequest);
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getSenha(), userEntity.getSenha());
    }
    @Test
    @DisplayName("Tratar exceção ao buscar usuário por e-mail")
    public void testFindByEmail_ExceptionHandling() {
        UserDto loginRequest = new UserDto(null, null, null, "john@example.com", "password", null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenThrow(new RuntimeException("Erro ao buscar usuário por e-mail"));
        UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> {
            userService.findByEmail(loginRequest);
        });
        assertEquals("Erro ao buscar usuário por e-mail: Erro ao buscar usuário por e-mail", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    @Test
    @DisplayName("Tratar exceção ao verificar existência por e-mail")
    public void testExistsByEmail_ExceptionHandling() {
        String email = "john@example.com";
        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Erro ao buscar usuário por e-mail"));
        UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> {
            userService.existsByEmail(email);
        });
        assertEquals("Erro ao registrar e-mail: Erro ao buscar usuário por e-mail", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
    @Test
    @DisplayName("Tratar exceção ao buscar usuário por e-mail e senha")
    public void testFindByEmailAndPassword_ExceptionHandling() {
        String email = "john@example.com";
        String senha = "password";
        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Erro ao buscar usuário por e-mail"));
        UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> {
            userService.findByEmailAndPassword(email, senha);
        });
        assertEquals("Erro ao buscar usuário por e-mail e senha: Erro ao buscar usuário por e-mail", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userMapper);
    }

}
