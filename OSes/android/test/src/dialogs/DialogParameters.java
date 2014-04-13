package dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import 	android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import net.sourceforge.fidocadj.R;
import layers.LayerDesc;
import globals.Globals;
import graphic.FontG;
import net.sourceforge.fidocadj.FidoEditor;


/**
 * <pre>
 * 
 * Allows to create a generic dialog, capable of displaying and let the user
 * modify the parameters of a graphic primitive. The idea is that the dialog
 * uses a ParameterDescripion vector which contains all the elements, their
 * description as well as the type. Depending on the contents of the array, the
 * window will be created automatically.
 * 
 * </pre>
 *
 * @author Dante Loi
 *
 */

public class DialogParameters extends DialogFragment 
{
	private static Vector<ParameterDescription> vec;
	private static boolean strict;
	private static Vector<LayerDesc> layers;
	
	private static final int MAX = 20;
	
	private static final int BORDER = 30;
	private static final int MAX_LEN = 200;
	// Maximum number of user interface elements of the same type present
	// in the dialog window.
	private static final int MAX_ELEMENTS = 100;

	// Text box array and counter
	private EditText etv[];
	private int ec;	
	
	// Check box array and counter
	private CheckBox cbv[];
	private int cc; 

	private Spinner spv[];
	private int sc; 
	
	private FidoEditor caller;
	
	/**
	 * Get a ParameterDescription vector describing the characteristics modified
	 * by the user.
	 * 
	 * @return a ParameterDescription vector describing each parameter.
	 */
	public Vector<ParameterDescription> getCharacteristics() {
		return vec;
	}	
	
	/**
	 * Makes the dialog and passes its arguments to it.
	 * 
	 * @return a new istance of DialogParameters.
	 */
	public static DialogParameters newInstance(Vector<ParameterDescription> vec,
			boolean strict, Vector<LayerDesc> layers, FidoEditor callback) 
	{
		DialogParameters dialog = new DialogParameters();
		
		Bundle args = new Bundle();
        args.putSerializable("vec", vec);
        args.putBoolean("strict", strict);
        args.putSerializable("layers", layers);
        dialog.setArguments(args);
        dialog.caller=callback;
		
		return dialog;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{  
    	super.onCreate(savedInstanceState);
    	
    	if(savedInstanceState != null){
    		vec = (Vector<ParameterDescription>) savedInstanceState.getSerializable("vec");
            strict = savedInstanceState.getBoolean("strict");
            layers = (Vector<LayerDesc>) savedInstanceState.getSerializable("layers");
    	}
        else{
        	vec = (Vector<ParameterDescription>) getArguments().getSerializable("vec");
        	strict = getArguments().getBoolean("strict");
        	layers = (Vector<LayerDesc>) getArguments().getSerializable("layers");
        }
    	
		final Activity context = getActivity();
		final Dialog dialog = new Dialog(context);
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	
    	LinearLayout vv = new LinearLayout(context);
    	
    	vv.setOrientation(LinearLayout.VERTICAL);
    	/*vv.setMinimumHeight(getResources().
			getDimensionPixelSize(R.dimen.large_dialog_height));*/
    	vv.setMinimumWidth(getResources().
			getDimensionPixelSize(R.dimen.normal_dialog_width));
    	vv.setBackgroundColor(getResources().
			getColor(R.color.background_white));
    	vv.setPadding(20, BORDER, BORDER, 15);
    	
    	etv = new EditText[MAX_ELEMENTS];
		cbv = new CheckBox[MAX_ELEMENTS];
		spv = new Spinner[MAX_ELEMENTS];
    	
		ParameterDescription pd;

		TextView lab;

		ec = 0;
		cc = 0;
		sc = 0;

		// We process all parameter passed. Depending on its type, a
		// corresponding interface element will be created.
		// A symmetrical operation is done when validating parameters.
		for (int ycount = 0; ycount < vec.size(); ++ycount) {
			if (ycount > MAX)
				break;

			pd = (ParameterDescription) vec.elementAt(ycount);
    	
			// We do not need to store label objects, since we do not need
			// to retrieve data from them.

			lab = new TextView(context);
			lab.setTextColor(Color.BLACK);
			lab.setText(pd.description);
			lab.setPadding(0, 0, 10, 0);
			//lab.setMinimumHeight(100);
			//lab.setMinimumWidth(100);
			
			LinearLayout vh = new LinearLayout(context);
			vh.setGravity(Gravity.FILL_HORIZONTAL);
			
			int background = Color.GREEN;
			
			if (!(pd.parameter instanceof Boolean))
				vh.addView(lab);
			// Now, depending on the type of parameter we create interface
			// elements and we populate the dialog.

			if (pd.parameter instanceof Point) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundColor(background);
				etv[ec].setText("" + ((Point) (pd.parameter)).x);
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setSingleLine();
				etv[ec].setPadding(0, 0, 10, 0);

				vh.addView(etv[ec++]);

				etv[ec] = new EditText(context);
				etv[ec].setText("" + ((Point) (pd.parameter)).y);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundColor(background);
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setSingleLine();
				etv[ec].setPadding(0, 0, 10, 0);
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof String) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setGravity(Gravity.FILL_HORIZONTAL|
					Gravity.CENTER_HORIZONTAL);
				etv[ec].setBackgroundColor(background);
				etv[ec].setText((String) (pd.parameter));
				//etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setSingleLine();
				etv[ec].setPadding(0, 0, 10, 0);
				// If we have a String text field in the first position, its
				// contents should be evidenced, since it is supposed to be
				// the most important field (e.g. for the AdvText primitive)
				if (ycount == 0)
					etv[ec].selectAll();
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof Boolean) {
				cbv[cc] = new CheckBox(context);
				cbv[cc].setText(pd.description);
				cbv[cc].setTextColor(Color.BLACK);
				cbv[cc].setChecked(((Boolean) (pd.parameter)).booleanValue());
				
				vh.setPadding(50, 0, 10, 0);
				vh.addView(cbv[cc++]);

			} else if (pd.parameter instanceof Integer) {
				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundColor(background);
				etv[ec].setText(((Integer) pd.parameter).toString());
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setSingleLine();
				etv[ec].setPadding(10, 0, 10, 0);
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof Float) {

				etv[ec] = new EditText(context);
				etv[ec].setTextColor(Color.BLACK);
				etv[ec].setBackgroundColor(background);
				int dummy = java.lang.Math.round((Float) pd.parameter);
				etv[ec].setText(""+dummy);
				etv[ec].setMaxWidth(MAX_LEN);
				etv[ec].setSingleLine();
				etv[ec].setPadding(0, 0, 0, 0);
				
				vh.addView(etv[ec++]);
			} else if (pd.parameter instanceof FontG) {
				spv[sc] = new Spinner(context);
				
				String[] s = {"Normal","Italic","Bold"};			
			
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, android.R.layout.simple_spinner_item , s);
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundColor(background);
				
				for (int i = 0; i < s.length; ++i) {
					if (s[i].equals(((FontG) pd.parameter).getFamily()))
						spv[sc].setSelection(i);
					else 
						spv[sc].setSelection(0);
				}
				vh.addView(spv[sc++]);
				
			} else if (pd.parameter instanceof LayerInfo) {
				spv[sc] = new Spinner(context);
				List<String> l = new ArrayList<String>();
				
				for (int i = 0; i < layers.size(); i++)
					l.add(layers.get(i).getDescription());
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, 
						android.R.layout.simple_spinner_item, l);
				
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundColor(background);
				spv[sc].setSelection(((LayerInfo) pd.parameter).layer);
				
				vh.addView(spv[sc++]);
		    
			} else if (pd.parameter instanceof ArrowInfo) {
				spv[sc] = new Spinner(context);
				
				List<ArrowInfo> l = new ArrayList<ArrowInfo>();
				l.add(new ArrowInfo(0));
				l.add(new ArrowInfo(1));
				l.add(new ArrowInfo(2));
				l.add(new ArrowInfo(3));
				
				ArrayAdapter<ArrowInfo> adapter = new ArrayAdapter<ArrowInfo>(context, 
						android.R.layout.simple_spinner_item, l);
				
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundColor(background);
				spv[sc].setSelection(((ArrowInfo) pd.parameter).style);
				
				vh.addView(spv[sc++]);
				
			} else if (pd.parameter instanceof DashInfo) {
				spv[sc] = new Spinner(context);
				
				List<DashInfo> l = new ArrayList<DashInfo>();
				for (int k = 0; k < Globals.dashNumber; ++k)
					l.add(new DashInfo(k));
					
				ArrayAdapter<DashInfo> adapter = new ArrayAdapter<DashInfo>(context, 
						android.R.layout.simple_spinner_item, l);
				
				spv[sc].setAdapter(adapter);
				spv[sc].setBackgroundColor(background);
				spv[sc].setSelection(((DashInfo) pd.parameter).style);
				
				vh.addView(spv[sc++]);
			}
			vv.addView(vh);
		}
    	
		LinearLayout buttonView = new LinearLayout(context);
		buttonView.setGravity(Gravity.RIGHT);
		buttonView.setOrientation(LinearLayout.HORIZONTAL);
		
		Button ok = new Button(context);  
		ok.setText(getResources().getText(R.string.Ok_btn));
		ok.setOnClickListener(new OnClickListener() 
		{  
			@Override  
			public void onClick(View buttonView) 
			{  
				try {
					int ycount;
					ParameterDescription pd;
					
					ec = 0;
					cc = 0;
					sc = 0;

					// Here we read all the contents of the interface and we
					// update the contents of the parameter description array.

					for (ycount = 0; ycount < vec.size(); ++ycount) {
						if (ycount > MAX)
							break;
						pd = (ParameterDescription) vec.elementAt(ycount);

						if (pd.parameter instanceof Point) {
							((Point) (pd.parameter)).x = Integer
									.parseInt(etv[ec++].getText().toString());
							((Point) (pd.parameter)).y = Integer
									.parseInt(etv[ec++].getText().toString());
						} else if (pd.parameter instanceof String) {
							pd.parameter = etv[ec++].getText().toString();
						} else if (pd.parameter instanceof Boolean) {
							android.util.Log.e("fidocadj", 
								"value:"+Boolean.valueOf(
								cbv[cc].isChecked()));
							pd.parameter = Boolean.valueOf(
								cbv[cc++].isChecked());
							
						} else if (pd.parameter instanceof Integer) {
							pd.parameter = Integer.valueOf(Integer
									.parseInt(etv[ec++].getText().toString()));
						} else if (pd.parameter instanceof Float) {
							pd.parameter = Float.valueOf(Float
									.parseFloat(etv[ec++].getText().toString()));
						} else if (pd.parameter instanceof FontG) {
							pd.parameter = new FontG((String) spv[sc++]
									.getSelectedItem());
						} else if (pd.parameter instanceof LayerInfo) {
							pd.parameter = new LayerInfo((Integer) spv[sc++]
									.getSelectedItemPosition());
						} else if (pd.parameter instanceof ArrowInfo) {
							pd.parameter = new ArrowInfo((Integer) spv[sc++]
									.getSelectedItemPosition());
						} else if (pd.parameter instanceof DashInfo) {
							pd.parameter = new DashInfo((Integer) spv[sc++]
									.getSelectedItemPosition());
						}
						else {}
					}
				} catch (NumberFormatException E) {
					// Error detected. Probably, the user has entered an
					// invalid string when FidoCadJ was expecting a numerical
					// input.

					Toast t = new Toast(context);
					t.setText(Globals.messages.getString("Format_invalid"));
					t.show();
				}
				caller.saveCharacteristics(vec);
				dialog.dismiss();
			}
		});
		
		buttonView.addView(ok);
		
		Button cancel = new Button(context);  
		cancel.setText(getResources().getText(R.string.Cancel_btn));
		cancel.setOnClickListener(new OnClickListener() 
		{  
			@Override  
			public void onClick(View buttonView) 
			{  
				dialog.dismiss();
			}  
		});
		buttonView.addView(cancel);
		
		vv.addView(buttonView);
		
		dialog.setContentView((View)vv);  
		
		return dialog;
	}
	
	public void onDismiss(Bundle savedInstanceState)
	{
		savedInstanceState.putSerializable("vec", vec);
		savedInstanceState.putBoolean("strict", strict);
		savedInstanceState.putSerializable("layers", layers);
	}
}