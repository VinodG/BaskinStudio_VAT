package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.databaseaccess.DictionaryEntry;
import com.winit.baskinrobbin.salesman.dataobject.NotesObject;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class NotesDA 
{
	private Vector<NotesObject> vctNotesList;
	private NotesObject notesObject;
	public void insertNotes(NotesObject objNotesObject)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				int noteId = 1;
				DictionaryEntry[][] data=null;
				data = DatabaseHelper.get("SELECT max(NoteId) from tblNotes");
				
				if(data!=null && data.length>0 && data[0][0].value!=null)
					noteId = StringUtils.getInt(data[0][0].value.toString())+1;
				
				objSqliteDB = DatabaseHelper.openDataBase();
					
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblNotes (NoteId, CustomerId, PresellerId, NoteDate, Subject,Description,Image) VALUES(?,?,?,?,?,?,?)");
				if(objNotesObject!=null)
				{
					if(objNotesObject.Note_Id==0)
						stmtInsert.bindLong(1, noteId);
					else
						stmtInsert.bindLong(1, objNotesObject.Note_Id);
					stmtInsert.bindString(2, objNotesObject.Customer_ID);
					stmtInsert.bindString(3, objNotesObject.Emp_Id);
					stmtInsert.bindString(4,objNotesObject.DateAndTime);
					stmtInsert.bindString(5,objNotesObject.Note_Title);
					stmtInsert.bindString(6, objNotesObject.Note_Description);
					stmtInsert.bindString(7, objNotesObject.image);
					
					stmtInsert.executeInsert();
					
					stmtInsert.close();
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	public boolean insertNotes(Vector<NotesObject> vecNotesObjects)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =  null;
			try
			{
				objSqliteDB =  DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblNotes WHERE NoteId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblNotes (NoteId, CustomerId, PresellerId, NoteDate, Subject,Description,Image) VALUES(?,?,?,?,?,?,?)");
				
				for(int i=0;i<vecNotesObjects.size();i++)
				{
					NotesObject objNotesObject = vecNotesObjects.get(i);
					stmtSelectRec.bindString(1, ""+objNotesObject.Note_Id);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{
						
					}
					else
					{
						if(objNotesObject!=null)
						{
							stmtInsert.bindLong(1, objNotesObject.Note_Id);
							stmtInsert.bindString(2, objNotesObject.Customer_ID);
							stmtInsert.bindString(3, objNotesObject.Emp_Id);
							stmtInsert.bindString(4,objNotesObject.DateAndTime);
							stmtInsert.bindString(5,objNotesObject.Note_Title);
							stmtInsert.bindString(6, objNotesObject.Note_Description);
							stmtInsert.bindString(7, objNotesObject.image);
							stmtInsert.executeInsert();
						}
					}
				}
						
				
				stmtInsert.close();
				stmtSelectRec.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
				{
					
					objSqliteDB.close();
					return true;
				}
			}
			return true;
		}
	}
	public int getNextNotesId()
	{
		synchronized(MyApplication.MyLock) 
		{
			int nextId=0;
			DictionaryEntry [][] data = DatabaseHelper.get("select  MAX(Note_Id) from Notes"); 
			if(data != null && data[0][0].value != null)
			{
				nextId	=	StringUtils.getInt(data[0][0].value.toString());
			}
			return (nextId+1);
		}
	}
	
	public Vector<NotesObject> getNotesList(String strCutomerID,String Emp_Id)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				vctNotesList= new Vector<NotesObject>();
				DictionaryEntry[][] data=null;
				data = DatabaseHelper.get("select * from tblNotes where CustomerId='"+strCutomerID+"' AND PresellerId='"+Emp_Id+"'");
				if(data !=null && data.length>0)
				{
					for(int i=0;i<data.length;i++)
					{
						notesObject 					= 	new NotesObject();
						notesObject.Note_Id				=	StringUtils.getInt(data[i][0].value.toString());
						notesObject.DateAndTime			=	data[i][3].value.toString();
						notesObject.Note_Title			=	data[i][4].value.toString();
						notesObject.Note_Description	=	data[i][5].value.toString();
						notesObject.image				=	data[i][6].value.toString();
						
						vctNotesList.add(notesObject);
					}	
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return vctNotesList;
		}
	}
	
	public boolean deleteNote(String noteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				DatabaseHelper.get("delete from tblNotes where NoteId = '"+noteId+"'");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		    return true;
		}
	}
}
