package org.ignia.comprobante.computos;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.ItemComputo.ItemComputoModel;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComputoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "computo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemComputoModel> items = new ArrayList<>();

}
