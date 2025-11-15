package com.idgs12.usuarios.usuarios.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idgs12.usuarios.usuarios.dto.UsuarioDTO;
import com.idgs12.usuarios.usuarios.entity.UsuarioEntity;
import com.idgs12.usuarios.usuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public UsuarioEntity crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.saveUsuarioConProgramas(usuarioDTO);
    }

}
