"use client";

import { useState, useEffect } from "react";
import {
  Box,
  Button,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  IconButton,
  Snackbar,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import { Edit, Delete } from "@mui/icons-material";

interface EmailList {
  id: number;
  name: string;
  recipients: string[];
}

interface SnackbarState {
  open: boolean;
  message: string;
  severity: "success" | "error";
}

const EmailListManager = () => {
  const [emailLists, setEmailLists] = useState<EmailList[]>([]);
  const [name, setName] = useState("");
  const [recipients, setRecipients] = useState("");
  const [editId, setEditId] = useState<number | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [snackbar, setSnackbar] = useState<SnackbarState>({
    open: false,
    message: "",
    severity: "success",
  });

  // Fetch email lists on mount
  useEffect(() => {
    const fetchEmailLists = async () => {
      try {
        const res = await fetch("http://localhost:8081/api/email-lists", {
          headers: { "Content-Type": "application/json" },
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const data: EmailList[] = await res.json();
        setEmailLists(data);
      } catch (error) {
        setSnackbar({
          open: true,
          message: "Failed to fetch email lists",
          severity: "error",
        });
      }
    };
    fetchEmailLists();
  }, []);

  // Handle create or update email list
  const handleSave = async () => {
    if (!name || !recipients) {
      setSnackbar({
        open: true,
        message: "Name and recipients are required",
        severity: "error",
      });
      return;
    }

    const recipientList = recipients
      .split(",")
      .map((email) => email.trim())
      .filter((email) => email);
    if (recipientList.length === 0) {
      setSnackbar({
        open: true,
        message: "At least one valid email is required",
        severity: "error",
      });
      return;
    }

    const emailList = { name, recipients: recipientList };
    const url = editId
      ? `http://localhost:8081/api/email-lists/${editId}`
      : "http://localhost:8081/api/email-lists";
    const method = editId ? "PUT" : "POST";

    try {
      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(emailList),
      });
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      const saved: EmailList = await res.json();

      if (editId) {
        setEmailLists((prev) =>
          prev.map((list) => (list.id === editId ? saved : list))
        );
      } else {
        setEmailLists((prev) => [...prev, saved]);
      }

      setSnackbar({
        open: true,
        message: editId ? "Email list updated" : "Email list created",
        severity: "success",
      });
      resetForm();
    } catch (error) {
      setSnackbar({
        open: true,
        message: `Failed to ${editId ? "update" : "create"} email list`,
        severity: "error",
      });
    }
  };

  // Handle edit
  const handleEdit = (list: EmailList) => {
    setEditId(list.id);
    setName(list.name);
    setRecipients(list.recipients.join(", "));
    setOpenDialog(true);
  };

  // Handle delete
  const handleDelete = async (id: number) => {
    try {
      const res = await fetch(`http://localhost:8081/api/email-lists/${id}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      setEmailLists((prev) => prev.filter((list) => list.id !== id));
      setSnackbar({
        open: true,
        message: "Email list deleted",
        severity: "success",
      });
    } catch (error) {
      setSnackbar({
        open: true,
        message: "Failed to delete email list",
        severity: "error",
      });
    }
  };

  // Reset form and close dialog
  const resetForm = () => {
    setName("");
    setRecipients("");
    setEditId(null);
    setOpenDialog(false);
  };

  return (
    <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
      <Button
        variant="contained"
        onClick={() => setOpenDialog(true)}
        sx={{ alignSelf: "flex-start" }}
      >
        Create Email List
      </Button>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Recipients</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {emailLists.map((list) => (
            <TableRow key={list.id}>
              <TableCell>{list.name}</TableCell>
              <TableCell>{list.recipients.join(", ")}</TableCell>
              <TableCell>
                <IconButton onClick={() => handleEdit(list)}>
                  <Edit />
                </IconButton>
                <IconButton onClick={() => handleDelete(list.id)}>
                  <Delete />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Dialog open={openDialog} onClose={resetForm}>
        <DialogTitle>{editId ? "Edit Email List" : "Create Email List"}</DialogTitle>
        <DialogContent>
          <TextField
            label="List Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            fullWidth
            margin="normal"
          />
          <TextField
            label="Recipient Emails (comma-separated)"
            value={recipients}
            onChange={(e) => setRecipients(e.target.value)}
            fullWidth
            margin="normal"
            multiline
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={resetForm}>Cancel</Button>
          <Button onClick={handleSave} variant="contained">
            {editId ? "Update" : "Create"}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert severity={snackbar.severity}>{snackbar.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default EmailListManager;