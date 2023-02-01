package com.daniel.db.model;

import lombok.*;

import javax.persistence.*;

@ToString
@EqualsAndHashCode
@Setter
@Getter
@Entity
@NoArgsConstructor
/**
 * Class the represents the customer table inside SQLLite database
 * @author dfcastro
 *
 */
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length = 50)
	private String name;
	@Column(length = 50)
	private String phone;
	@Column(name="valid")
	private Integer valid;
	@Column
	private Integer code;	
	public Customer(Long id, String name, String phone) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
	}
}
