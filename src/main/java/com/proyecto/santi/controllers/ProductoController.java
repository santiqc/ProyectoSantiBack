package com.proyecto.santi.controllers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.santi.dto.Mensaje;
import com.proyecto.santi.dto.ProductoDto;
import com.proyecto.santi.entity.Producto;
import com.proyecto.santi.service.ProductoService;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;




@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {
	 @Autowired
	    ProductoService productoService;
	 
	 	@ApiOperation("muestra una lista de productos")
	    @GetMapping("/lista")
	    public ResponseEntity<List<Producto>> list(){
	        List<Producto> list = productoService.list();
	        return new ResponseEntity(list, HttpStatus.OK);
	    }
	 	@ApiIgnore
	    @GetMapping("/detail/{id}")
	    public ResponseEntity<Producto> getById(@PathVariable("id") int id){
	        if(!productoService.existsById(id))
	            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
	        Producto producto = productoService.getOne(id).get();
	        return new ResponseEntity(producto, HttpStatus.OK);
	    }
	 	@ApiIgnore 
	    @GetMapping("/detailname/{nombre}")
	    public ResponseEntity<Producto> getByNombre(@PathVariable("nombre") String nombre){
	        if(!productoService.existsByNombre(nombre))
	            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
	        Producto producto = productoService.getByNombre(nombre).get();
	        return new ResponseEntity(producto, HttpStatus.OK);
	    }
	    
	    @PreAuthorize("hasRole('ADMIN')")
	    @PostMapping("/create")
	    public ResponseEntity<?> create(@RequestBody ProductoDto productoDto){
	        if(StringUtils.isBlank(productoDto.getNombre()))
	            return new ResponseEntity(new Mensaje("el nombre es obligatorio"), HttpStatus.BAD_REQUEST);
	        if(productoDto.getPrecio()==null || productoDto.getPrecio()<0 )
	            return new ResponseEntity(new Mensaje("el precio debe ser mayor que 0"), HttpStatus.BAD_REQUEST);
	        if(productoService.existsByNombre(productoDto.getNombre()))
	            return new ResponseEntity(new Mensaje("ese nombre ya existe"), HttpStatus.BAD_REQUEST);
	        Producto producto = new Producto(productoDto.getNombre(), productoDto.getPrecio());
	        productoService.save(producto);
	        return new ResponseEntity(new Mensaje("producto creado"), HttpStatus.OK);
	    }
	    @PreAuthorize("hasRole('ADMIN')")
	    @PutMapping("/update/{id}")
	    public ResponseEntity<?> update(@PathVariable("id")int id, @RequestBody ProductoDto productoDto){
	        if(!productoService.existsById(id))
	            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
	        if(productoService.existsByNombre(productoDto.getNombre()) && productoService.getByNombre(productoDto.getNombre()).get().getId() != id)
	            return new ResponseEntity(new Mensaje("ese nombre ya existe"), HttpStatus.BAD_REQUEST);
	        if(StringUtils.isBlank(productoDto.getNombre()))
	            return new ResponseEntity(new Mensaje("el nombre es obligatorio"), HttpStatus.BAD_REQUEST);
	        if(productoDto.getPrecio()==null || productoDto.getPrecio()<0 )
	            return new ResponseEntity(new Mensaje("el precio debe ser mayor que 0"), HttpStatus.BAD_REQUEST);

	        Producto producto = productoService.getOne(id).get();
	        producto.setNombre(productoDto.getNombre());
	        producto.setPrecio(productoDto.getPrecio());
	        productoService.save(producto);
	        return new ResponseEntity(new Mensaje("producto actualizado"), HttpStatus.OK);
	    }
	    @PreAuthorize("hasRole('ADMIN')")
	    @DeleteMapping("/delete/{id}")
	    public ResponseEntity<?> delete(@PathVariable("id")int id){
	        if(!productoService.existsById(id))
	            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
	        productoService.delete(id);
	        return new ResponseEntity(new Mensaje("producto eliminado"), HttpStatus.OK);
	    }
	

}
