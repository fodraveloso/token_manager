package br.com.security.infra.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.security.infra.entities.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

}
