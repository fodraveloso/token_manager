package br.com.security.infra.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "Role")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RoleEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@ManyToMany(mappedBy = "roles")
	private Set<UserEntity> users;

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@JoinTable(name = "role_authority", joinColumns = {
			@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID") })
	private Set<AuthorityEntity> authorities;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private UserEntity createdBy;

	@Builder
	private RoleEntity(String name, Set<AuthorityEntity> authorities, UserEntity createdBy) {

		this.name = name;
		this.authorities = Objects.isNull(authorities) ? new HashSet<>() : authorities;
		this.createdBy = createdBy;
	}
	
	public void addAuthority(AuthorityEntity authorityEntity) {
		
		this.authorities.add(authorityEntity);
	}
}
