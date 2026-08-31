package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;
import reservas.presentation.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String CURRENT = "current";
    public static final String FUNCIONARIOS = "funcionarios";

    private Funcionario current;
    private List<Funcionario> funcionarios;

    public Model() {
        current = new Funcionario();
        funcionarios = new ArrayList<>();
    }

    public Funcionario getCurrent() {
        return current;
    }

    public void setCurrent(Funcionario current) {

        this.current = current != null
                ? current
                : new Funcionario();

        firePropertyChange(CURRENT);
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {

        this.funcionarios = funcionarios != null
                ? new ArrayList<>(funcionarios)
                : new ArrayList<>();

        firePropertyChange(FUNCIONARIOS);
    }
}