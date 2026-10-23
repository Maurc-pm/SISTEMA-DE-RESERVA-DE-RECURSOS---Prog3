package reservas.logic;

public class Sesion {

        private static Usuario usuario;

        public static Usuario getUsuario() {
            return usuario;
        }

        public static void setUsuario(Usuario usuario) {
            Sesion.usuario = usuario;
        }

        public static boolean isLogged() {
            return usuario != null;
        }

        public static void logout() {
            usuario = null;
        }
}
