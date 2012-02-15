package mvc;


/**
 * 
 * Initialize the MVC system and begin all of the separate threads. And static
 * utility functions and variables should be written here as well.
 * 
 * @author Dylan Swiggett
 * 
 */
public class Main extends Thread {
	Model model;
	View view;
	Controller controller;

	public static void main(String[] args) {
		new Main().start();
	}

	public void run() {
		
		model = new Model();
		view = new View(model);
		controller = new Controller(model, view);
		
		model.start();
		controller.start();
		view.start();
		
		while (true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
//			view.repaint();
		}
	}

}
