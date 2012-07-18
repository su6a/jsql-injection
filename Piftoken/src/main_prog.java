import sqli.controller.InjectionController;
import sqli.model.InjectionModel;
import sqli.view.InjectionViewConsole;
import sqli.view.InjectionViewGUI;


public class main_prog {

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	InjectionModel model = new InjectionModel();
        		InjectionController controller = new InjectionController(model);
        		InjectionViewGUI gui = new InjectionViewGUI(controller);
        		InjectionViewConsole console = new InjectionViewConsole();

        		model.addObserver(gui);
        		model.addObserver(console);
            }
        });
	}

}
