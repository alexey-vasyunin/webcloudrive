package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.entity.Role;
import ru.vasyunin.springcloudrive.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRolesByName(String rolename){
        return roleRepository.findAllByRoleName(rolename);
    }
}
