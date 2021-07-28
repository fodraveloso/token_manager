package br.com.security.infra.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "Authority")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AuthorityEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String permissions;
	
	@ManyToMany(mappedBy = "authorities")
	private Set<RoleEntity> roles;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private UserEntity createdBy;

	public AuthorityEntity(String permissions, UserEntity createdBy) {
		this.permissions = permissions;
		this.createdBy = createdBy;
	}
}
