package com.proyecto.santi.security.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.santi.security.entity.Usuario;
import com.proyecto.santi.security.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public Optional<Usuario> getByNombreUsuario(String nombreUsuario){
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }
    
    public Optional<Usuario> getByNombreUsuarioOrEmail(String nombreOrEmail){
        return usuarioRepository.findByNombreUsuarioOrEmail(nombreOrEmail,nombreOrEmail);
    }
    public Optional<Usuario> getByTokenPassword(String tokenPassword){
        return usuarioRepository.findByTokenPassword(tokenPassword);
    }
    public Optional<Usuario> getOne(int id){
    	return usuarioRepository.findById(id);
    }

    public boolean existsByNombreUsuario(String nombreUsuario){
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }
    
    public boolean existsById(int id) {
    	return usuarioRepository.existsById(id);
    }
    
    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }
    
    public List<Usuario> list(){
    	return usuarioRepository.findAll();
    }
    
    public void delete(int id) {
    	usuarioRepository.deleteById(id);
    }
    
    
    
}