import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * File: Job.java Author: Brian McGillen Date: September 26, 2017 Purpose:
 * Contains fields and methods for Job objects. Extends scanner constructor and
 * toString() method of parent class. Contains list of job requirements. When
 * run() method is called, starts a new thread containing this job and adds this
 * job to the jTable displaying all jobs generated in SeaPortProgram.java. Checks
 * if associated port has resources to fulfill job requirements and then runs
 * job as soon as target ship and required resources become available.
 * Contains methods to pause and cancel this job.
 */

public class Job extends Thing implements Runnable {

	double duration;
	ArrayList<String> requirements = new ArrayList<>();
	Ship targetShip = null;
	int jobProgress;
	Status status = Status.WAITING;
	boolean pauseFlag = true, noCancelFlag = true;
	Resources resources = null;
	int jobCount;

	JButton jb = new JButton("Pause");
	JButton cb = new JButton("Cancel");

	DefaultTableModel jobTableModel;

	enum Resources {
		ACQUIRED, WAITING, IMPOSSIBLE, RELEASED
	};
	enum Status {
		RUNNING, SUSPENDED, WAITING, DONE, CANCELED 
	};

	public Job(Scanner sc, DefaultTableModel jobTableModel, int jobCount) {
		// scan lines of job data for job name, duration, and requirements
		super(sc);
		if (sc.hasNextDouble())
			duration = sc.nextDouble();
		Scanner lineScan = new Scanner(sc.nextLine());
		while (lineScan.hasNext())
			requirements.add(lineScan.next());
		lineScan.close();

		JProgressBar jobProgress = new JProgressBar();
		jobProgress.setStringPainted(true);

		this.jobTableModel = jobTableModel;
		this.jobCount = jobCount;
	}

	// adds data to job progress jTable and starts the thread for this job
	void startThread() {
		addData(jobTableModel);
		new Thread(this).start();
	}

	// updates this job's status and displays the new status in the table
	void showStatus(Status st) {
		this.status = st;
		jobTableModel.setValueAt(st, jobCount, 7);
	}

	// cancels this job and displays the new status in the table
	public void setCancelFlag() {
		noCancelFlag = false;
		cb.setText("Cancelled");
		cb.setEnabled(false);
		jobTableModel.fireTableCellUpdated(jobCount, 9);
	}

	// pauses or resumes this job and displays the new status in the table
	public void togglePauseFlag() {
		pauseFlag = !pauseFlag;
		if (jb.getText().equals("Pause"))
			jb.setText("Resume");
		else
			jb.setText("Pause");
		jobTableModel.fireTableCellUpdated(jobCount, 8);
	}

	// updates the text displayed on the pause button if "pause all jobs"
	// or "resume all jobs" are called from SeaPortProgram.java
	public void setPauseButton(Boolean b) {
		if (b == false)
			jb.setText("Resume");
		else
			jb.setText("Pause");
		jobTableModel.fireTableCellUpdated(jobCount, 8);
	}

	// update acquired resources check box
	public void setAcquiredResourcesBox() {
		jobTableModel.setValueAt(this.resources, jobCount, 4);
		jobTableModel.fireTableCellUpdated(jobCount, 4);
	}

	@Override
	public void run() {
		// set time for this thread to run
		long time = System.currentTimeMillis();
		long startTime = time;
		long stopTime = (long) (time + 1000 * this.duration);
		double duration = stopTime - time;

		// wait if this job's ship is not assigned to a dock
		while (targetShip.dock == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// once this job's ship has a dock, update dock information
		// in the table
		for (int i = 0; i < jobTableModel.getRowCount(); i++) {
			if ((String) jobTableModel.getValueAt(i, 2) == targetShip.name) {
				jobTableModel.setValueAt(targetShip.dock.name, i, 1);
			}
		}

		// poll port resources to see if job will ever be possible
		resources = targetShip.port.pollResources(requirements, resources);
		
		// synchronize on this job's port; wait until no other job
		// is being performed on target ship and required resources
		// are available at port
		synchronized (targetShip.port) {
			if (resources == Resources.IMPOSSIBLE) {
				setCancelFlag();
			}
			else {
				resources = targetShip.port.getResources(this, requirements, resources);
				while (resources == Resources.WAITING || targetShip.busyFlag) {
					try {
						targetShip.port.wait();
						if (resources == Resources.WAITING) resources = targetShip.port.getResources(this, requirements, resources);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			targetShip.busyFlag = true;
			if (!requirements.isEmpty()) setAcquiredResourcesBox();
		}

		// as long as job has time remaining and isn't cancelled,
		// continue to run while updated progress bar
		while (time < stopTime && noCancelFlag) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			if (pauseFlag) {
				showStatus(Status.RUNNING);
				time += 100;
				jobProgress = ((int) (((time - startTime) * 100) / duration));
				jobTableModel.setValueAt(jobProgress, jobCount, 6);
			} else {
				showStatus(Status.SUSPENDED);
			}
		}

		// once job is completed or cancelled, update status on table and
		// notify other jobs waiting on this job's ship
		if (noCancelFlag == false)
			showStatus(Status.CANCELED);
		else
			showStatus(Status.DONE);
		
		// remove job from ship, release any resources being used,
		//and wake up other threads waiting to run
		synchronized (targetShip.port) {
			removeJobFromShip();
			targetShip.busyFlag = false;
			
			if (!requirements.isEmpty() && resources!= Resources.IMPOSSIBLE) {
				targetShip.port.releaseResources(this);
				resources=Resources.RELEASED;
				setAcquiredResourcesBox();
			}
			
			targetShip.port.notifyAll();
		}
	}

	public String toString() {
		return this.name;
	}

	// method to add this job's data to the job progress table
	public void addData(DefaultTableModel jobTableModel) {
		Object[] data = new Object[10];
		data[0] = this.targetShip.port.name;
		String dockData = "";
		if (this.targetShip.dock == null)
			dockData = "--";
		else
			dockData = this.targetShip.dock.name;
		data[1] = dockData;
		data[2] = this.targetShip.name;
		String requirements = "";
		for (String requirement : this.requirements) {
			requirements += (requirement + "\n");
		}
		data[3] = requirements;
		data[4] = resources;
		data[5] = this.duration;
		data[6] = 0;
		data[7] = this.status;
		data[8] = jb;
		data[9] = cb;

		// listeners for buttons embedded in table
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (status != Status.DONE) {
					togglePauseFlag();
				}
			}
		});

		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (status != Status.DONE) {
					setCancelFlag();
					showStatus(Status.CANCELED);
				}

			}
		});

		jobTableModel.addRow(data);
	}

	// method called when job has been completed or cancelled. When
	// all jobs assigned to a ship have been removed, removes the target
	// ship from its dock.
	private void removeJobFromShip() {
		targetShip.jobs.remove(this);
		if (targetShip.jobs.isEmpty()) {
			targetShip.dock.replaceCurrentShip();
			for (int i = 0; i < jobTableModel.getRowCount(); i++) {
				if ((String) jobTableModel.getValueAt(i, 2) == targetShip.name) {
					jobTableModel.setValueAt("--", i, 1);
				}
			}
		}
	}

	// comparator for sorting job arrayList in order to present sorted view of jobs
	// in table - may use in project 4
	public static Comparator<Job> jobShipComparator = new Comparator<Job>() {

		public int compare(Job a, Job b) {
			return a.targetShip.name.compareTo(b.targetShip.name);
		}
	};
}

// class to render jButton in job progress table
class JButtonRenderer implements TableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JButton button = (JButton) value;
		return button;
	}
}

// class to listen for mouse click events on the jButton rendered
// in the above class. Sends a click event to the jButton contained
// in the jobClass

class JTableButtonMouseListener extends MouseAdapter {
	private final JTable table;

	public JTableButtonMouseListener(JTable table) {
		this.table = table;

	}

	public void mouseClicked(MouseEvent e) {
		int column = table.getColumnModel().getColumnIndexAtX(e.getX());
		int row = e.getY() / table.getRowHeight();

		// Checking the row or column is valid or not
		if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
			Object value = table.getValueAt(row, column);
			if (value instanceof JButton) {
				// perform a click event
				((JButton) value).doClick();

			}
		}
	}
}

// class to render jProgressBar in job progress table
class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	public ProgressBarRenderer() {
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setValue((int) value);
		return this;
	}
}
