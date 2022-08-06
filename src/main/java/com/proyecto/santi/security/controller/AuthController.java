package com.proyecto.santi.security.controller;


import com.nimbusds.jose.shaded.json.parser.ParseException;
import com.proyecto.santi.dto.Mensaje;
import com.proyecto.santi.security.dto.JwtDto;
import com.proyecto.santi.security.dto.LoginUsuario;
import com.proyecto.santi.security.dto.NuevoUsuario;
import com.proyecto.santi.security.entity.Rol;
import com.proyecto.santi.security.entity.Usuario;
import com.proyecto.santi.security.enums.RolNombre;
import com.proyecto.santi.security.jwt.JwtProvider;
import com.proyecto.santi.security.service.RolService;
import com.proyecto.santi.security.service.UsuarioService;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/nuevo")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("campos mal puestos o email inválido"), HttpStatus.BAD_REQUEST);
        if(usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario()))
            return new ResponseEntity(new Mensaje("ese nombre ya existe"), HttpStatus.BAD_REQUEST);
        if(usuarioService.existsByEmail(nuevoUsuario.getEmail()))
            return new ResponseEntity(new Mensaje("ese email ya existe"), HttpStatus.BAD_REQUEST);
        Usuario usuario =
                new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(), nuevoUsuario.getEmail(),
                        passwordEncoder.encode(nuevoUsuario.getPassword()));
        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        if(nuevoUsuario.getRoles().contains("admin"))
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
        usuario.setRoles(roles);
        usuarioService.save(usuario);
        return new ResponseEntity(new Mensaje("usuario guardado"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("campos mal puestos"), HttpStatus.BAD_REQUEST);
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        JwtDto jwtDto = new JwtDto(jwt);
        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws java.text.ParseException{
    	String token =jwtProvider.refreshToken(jwtDto);
    	JwtDto jwt = new JwtDto(token); 
		return new ResponseEntity(jwt, HttpStatus.OK);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Muestra la lista de usuarios")
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> list(){
    	List<Usuario> list = usuarioService.list();
    	return new ResponseEntity(list, HttpStatus.OK);
    }
    
    @ApiOperation("Elimina un usuario registrado")
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
    	if (!usuarioService.existsById(id)) {
    		return new ResponseEntity(new Mensaje("Usuario no existe"), HttpStatus.NOT_FOUND);
    		}
		usuarioService.delete(id);
		return new ResponseEntity(new Mensaje("Usuario eliminado con exito!"), HttpStatus.OK);
    }
    
    
  
    @ApiOperation("Muestra usuario por id")
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> getById(@PathVariable int id){
    	if (!usuarioService.existsById(id)) {
    		return new ResponseEntity(new Mensaje("Usuario no existe"), HttpStatus.NOT_FOUND);
    		}
    	Usuario usuario = usuarioService.getOne(id).get();
    	return new ResponseEntity(usuario, HttpStatus.OK);
    }
    
  
    @ApiOperation("Muestra usuario por nombre de usuario")
    @GetMapping("/usuarioname/{nombreUsuario}")
    public ResponseEntity<?> getByNombre(@PathVariable String nombreUsuario){
    	if (!usuarioService.existsByNombreUsuario(nombreUsuario)) {
    		return new ResponseEntity(new Mensaje("Usuario no existe"), HttpStatus.NOT_FOUND);
    		}
    	Usuario usuario = usuarioService.getByNombreUsuarioOrEmail(nombreUsuario).get();
    	return new ResponseEntity(usuario, HttpStatus.OK);
    }
    
    @ApiOperation("Usuario puede ser editado")
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> update(@Valid @PathVariable int id, @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("campos mal puestos o email inválido"), HttpStatus.BAD_REQUEST);
        if (!usuarioService.existsById(id)) {
        	 return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
		}
        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario()) && usuarioService.getByNombreUsuario(nuevoUsuario.getNombreUsuario()).get().getId() != id) {
        	return new ResponseEntity(new Mensaje("Ese nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);
			
		}
        if (usuarioService.existsByEmail(nuevoUsuario.getEmail()) && usuarioService.getByNombreUsuarioOrEmail(nuevoUsuario.getEmail()).get().getId() != id) {
        	return new ResponseEntity(new Mensaje("Ese email de usuario ya existe"), HttpStatus.BAD_REQUEST);
		}

        Usuario usuario = usuarioService.getOne(id).get();
        usuario.setNombre(nuevoUsuario.getNombre());
        usuario.setNombreUsuario(nuevoUsuario.getNombreUsuario());
        usuario.setEmail(nuevoUsuario.getEmail());
        usuarioService.save(usuario);
        return new ResponseEntity(new Mensaje("usuario actualizado"), HttpStatus.OK);
    }
    
    
}