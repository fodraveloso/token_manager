package br.com.security.infra.entities;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "User")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class UserEntity implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password;

	@Getter(AccessLevel.NONE)
	@ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = {
			@JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") })
	private Set<RoleEntity> roles;

	@Builder.Default
	@Column(nullable = false)
	private boolean accountNonExpired = true;

	@Builder.Default 
	@Column(nullable = false)
	private boolean accountNonLocked = true;

	@Builder.Default 
	@Column(nullable = false)
	private boolean credentialsNonExpired = true;

	@Builder.Default
	@Column(nullable = false)
	private boolean enabled = true;

	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp createdDate;

	@UpdateTimestamp
	private Timestamp lastModifiedDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity createdBy;

	@Override
	public void eraseCredentials() {

		this.password = null;
	}

	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return this.roles.stream().map(RoleEntity::getAuthorities).flatMap(Set::stream)
				.map(authority -> new SimpleGrantedAuthority(authority.getPermissions())).collect(Collectors.toSet());
	}
}
