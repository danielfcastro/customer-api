package com.daniel.db.model;

import lombok.*;

@ToString
@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
/**
 * Class that represent the visualization of the data to be presented by the presentation layer
 * @author dfcastro
 *
 */
public class CustomerView {

	private Long id;
    private String name;
    private String phone;
    private String countryCode;
    private String country;
    private String state;
}
