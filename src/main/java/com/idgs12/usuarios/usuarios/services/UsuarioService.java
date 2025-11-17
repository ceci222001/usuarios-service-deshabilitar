package com.idgs12.usuarios.usuarios.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idgs12.usuarios.usuarios.dto.ProgramaDTO;
import com.idgs12.usuarios.usuarios.dto.UsuarioDTO;
import com.idgs12.usuarios.usuarios.dto.UsuarioResponseDTO;
import com.idgs12.usuarios.usuarios.repository.UsuarioRepository;
import com.idgs12.usuarios.usuarios.repository.ProgramaUsuarioRepository;
import com.idgs12.usuarios.usuarios.FeignClient.ProgramaFeignClient;

import com.idgs12.usuarios.usuarios.entity.UsuarioEntity;
import com.idgs12.usuarios.usuarios.entity.ProgramaUsuario;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProgramaUsuarioRepository programaUsuarioRepository;

    @Autowired
    private ProgramaFeignClient programaFeignClient;

    
    public List<UsuarioResponseDTO> getAllUsuariosDTO() {
        return usuarioRepository.findAll().stream().map(usuario -> {
            UsuarioResponseDTO dto = new UsuarioResponseDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setApellidoPaterno(usuario.getApellidoPaterno());
            dto.setApellidoMaterno(usuario.getApellidoMaterno());
            dto.setCorreo(usuario.getCorreo());
            dto.setRol(usuario.getRol());
            dto.setMatricula(usuario.getMatricula());

            if (usuario.getProgramas() != null && !usuario.getProgramas().isEmpty()) {
                List<Long> programaIds = usuario.getProgramas().stream()
                        .map(ProgramaUsuario::getProgramaId)
                        .collect(Collectors.toList());

                List<ProgramaDTO> programas = programaFeignClient.obtenerProgramasPorIds(programaIds)
                        .stream()
                        .map(p -> {
                            ProgramaDTO pdto = new ProgramaDTO();
                            pdto.setId(p.getId());
                            pdto.setNombre(p.getNombre());
                            return pdto;
                        }).collect(Collectors.toList());

                dto.setProgramas(programas);
            }

            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public UsuarioEntity saveUsuarioConProgramas(UsuarioDTO usuarioDTO) {
        UsuarioEntity usuario;

        if (usuarioDTO.getId() != 0) {
            usuario = usuarioRepository.findById(usuarioDTO.getId()).orElse(new UsuarioEntity());
        } else {
            usuario = new UsuarioEntity();
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellidoPaterno(usuarioDTO.getApellidoPaterno());
        usuario.setApellidoMaterno(usuarioDTO.getApellidoMaterno());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setMatricula(usuarioDTO.getMatricula());

        UsuarioEntity savedUsuario = usuarioRepository.save(usuario);

        programaUsuarioRepository.deleteByUsuario_Id(savedUsuario.getId());

        if (usuarioDTO.getProgramaIds() != null && !usuarioDTO.getProgramaIds().isEmpty()) {
            List<ProgramaUsuario> relaciones = usuarioDTO.getProgramaIds().stream()
                    .map(programaId -> {
                        ProgramaUsuario pu = new ProgramaUsuario();
                        pu.setUsuario(savedUsuario);
                        pu.setProgramaId(programaId);
                        return pu;
                    })
                    .collect(Collectors.toList());

            programaUsuarioRepository.saveAll(relaciones);
        }

        return savedUsuario;
    }
    public UsuarioResponseDTO getUsuarioDTOById(int id) {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null)
            return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellidoPaterno(usuario.getApellidoPaterno());
        dto.setApellidoMaterno(usuario.getApellidoMaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());
        dto.setMatricula(usuario.getMatricula());

        if (usuario.getProgramas() != null && !usuario.getProgramas().isEmpty()) {
            List<Long> programaIds = usuario.getProgramas().stream()
                    .map(ProgramaUsuario::getProgramaId)
                    .collect(Collectors.toList());

            List<ProgramaDTO> programas = programaFeignClient.obtenerProgramasPorIds(programaIds)
                    .stream()
                    .map(p -> {
                        ProgramaDTO pdto = new ProgramaDTO();
                        pdto.setId(p.getId());
                        pdto.setNombre(p.getNombre());
                        return pdto;
                    }).collect(Collectors.toList());

            dto.setProgramas(programas);
        }

        return dto;
    }
    
}
