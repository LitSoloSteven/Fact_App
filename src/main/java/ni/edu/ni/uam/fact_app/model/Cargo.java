package ni.edu.ni.uam.fact_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Cargo {
    private Integer id;
    private String nombre;
    private String descripcion;
}
