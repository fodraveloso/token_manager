package br.com.security.infra.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.security.infra.entities.AuthorityEntity;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

}
