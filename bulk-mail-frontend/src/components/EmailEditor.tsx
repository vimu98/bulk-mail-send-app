"use client";

import { useRef, useState, useEffect, useCallback } from "react";
import dynamic from "next/dynamic";
import {
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  CircularProgress,
  Box,
  Snackbar,
  Alert,
} from "@mui/material";
import { log } from "console";

// Dynamic import for Unlayer editor (no SSR)
const EmailEditor = dynamic(() => import("react-email-editor"), { ssr: false });

interface Template {
  id: number;
  name: string;
}

interface Design {
  body: {
    rows: Array<unknown>;
  };
  counters?: Record<string, number>;
  schemaVersion?: number;
  [key: string]: any;
}

interface SnackbarState {
  open: boolean;
  message: string;
  severity: "success" | "error";
}

const TemplateEditorComponent = () => {
  const editorRef = useRef<any>(null);
  const [isEditorLoading, setIsEditorLoading] = useState(true);
  const [templates, setTemplates] = useState<Template[]>([]);
  const [selectedTemplateId, setSelectedTemplateId] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [snackbar, setSnackbar] = useState<SnackbarState>({
    open: false,
    message: "",
    severity: "success",
  });

  // Fetch templates on mount
  useEffect(() => {    
    const fetchTemplates = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/templates", {
          headers: { "Content-Type": "application/json" },
        });
        
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const data: Template[] = await res.json();
        console.log("Templates fetched:", data);
        setTemplates(data);
      } catch (error) {
        console.error("Fetch templates failed:", error);
        setSnackbar({
          open: true,
          message: "Failed to load templates",
          severity: "error",
        });
      }
    };
    fetchTemplates();
  }, []);

  const onEditorReady = useCallback(() => {
    setIsEditorLoading(false);
    console.log("Unlayer editor ready");
  }, []);

  useEffect(() => {
    if (selectedTemplateId === "" && editorRef.current?.editor && !isEditorLoading) {
      console.log("Clearing editor: No template selected");
      const emptyDesign: Design = {
        body: {
          rows: [],
        },
      };
      editorRef.current.editor.loadDesign(emptyDesign);
      console.log("Editor cleared with empty design");
    }
  }, [selectedTemplateId, isEditorLoading]);

  const loadTemplate = useCallback(async () => {
    if (!selectedTemplateId) {
      setSnackbar({ open: true, message: "Select a template", severity: "error" });
      return;
    }
    if (!editorRef.current?.editor) {
      setSnackbar({ open: true, message: "Editor not ready", severity: "error" });
      return;
    }

    setIsFetching(true);
    try {
      const res = await fetch(`http://localhost:8080/api/templates/${selectedTemplateId}`, {
        headers: { Accept: "application/json" },
      });
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      const responseData = await res.json();
      console.log("Raw backend response:", responseData);
      console.log("Raw backend response type:", typeof responseData);

      const design: Design = responseData.design;
      console.log("Processed design:", design);
      console.log("Processed design type:", typeof design);

      if (!design) {
        throw new Error("Invalid design: Design is null or undefined");
      }
      if (typeof design !== "object" || Array.isArray(design) || design === null) {
        throw new Error(`Invalid design: Design is not an object (received type: ${typeof design})`);
      }
      if (!design.body) {
        throw new Error("Invalid design: Missing body property");
      }
      if (typeof design.body !== "object" || Array.isArray(design.body) || design.body === null) {
        throw new Error("Invalid design: Body is not an object");
      }
      if (design.body.rows === undefined || design.body.rows === null) {
        throw new Error("Invalid design: Rows property is missing");
      }
      if (!Array.isArray(design.body.rows)) {
        throw new Error(`Invalid design: Rows is not an array (received type: ${typeof design.body.rows})`);
      }

      console.log("Design validated: body.rows is an array with length", design.body.rows.length);

      editorRef.current.editor.loadDesign(design);
      console.log("Design loaded into editor");

      editorRef.current.editor.exportHtml(({ html, design: loadedDesign }: { html: string; design: Design }) => {
        console.log("Loaded HTML:", html.slice(0, 100) + "...");
        console.log("Loaded design:", JSON.stringify(loadedDesign).slice(0, 100) + "...");
      });

      setSnackbar({ open: true, message: "Template loaded", severity: "success" });
    } catch (error: any) {
      console.error("Load template failed:", error);
      const fallbackDesign = {
        body: {
          rows: [
            {
              cells: [1],
              columns: [{ contents: [{ type: "text", values: { text: "Failed to load template" } }] }],
            },
          ],
        },
      };
      editorRef.current.editor.loadDesign(fallbackDesign);
      setSnackbar({
        open: true,
        message: `Load failed: ${error.message}`,
        severity: "error",
      });
    } finally {
      setIsFetching(false);
    }
  }, [selectedTemplateId]);

  const saveNewTemplate = useCallback(async () => {
    const template = templates.find((t) => t.id === Number(selectedTemplateId));
    const defaultName = template?.name || "email-template";
    const name = prompt("Enter template name", defaultName);
    if (!name) {
      setSnackbar({ open: true, message: "Name required", severity: "error" });
      return;
    }

    try {
      editorRef.current?.editor?.exportHtml(async ({ design }: { design: Design }) => {
        const blob = new Blob([JSON.stringify(design, null, 2)], { type: "application/json" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `${name}-${Date.now()}.json`;
        link.click();
        URL.revokeObjectURL(url);

        const res = await fetch("http://localhost:8080/api/templates", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ name, design }),
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const saved: Template = await res.json();
        setTemplates((prev) => [...prev, saved]);
        setSnackbar({ open: true, message: "New template saved", severity: "success" });
      });
    } catch (error: any) {
      console.error("Save new template failed:", error);
      setSnackbar({ open: true, message: `Save failed: ${error.message}`, severity: "error" });
    }
  }, [templates, selectedTemplateId]);

  const updateTemplate = useCallback(async () => {
    if (!selectedTemplateId) {
      setSnackbar({ open: true, message: "Select a template to update", severity: "error" });
      return;
    }

    const template = templates.find((t) => t.id === Number(selectedTemplateId));
    const defaultName = template?.name || "email-template";
    const name = prompt("Enter template name", defaultName);
    if (!name) {
      setSnackbar({ open: true, message: "Name required", severity: "error" });
      return;
    }

    try {
      editorRef.current?.editor?.exportHtml(async ({ design }: { design: Design }) => {
        const res = await fetch(`http://localhost:8080/api/templates/${selectedTemplateId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ name, design }),
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const updated: Template = await res.json();
        setTemplates((prev) =>
          prev.map((t) => (t.id === updated.id ? updated : t))
        );
        setSnackbar({ open: true, message: "Template updated", severity: "success" });
      });
    } catch (error: any) {
      console.error("Update template failed:", error);
      setSnackbar({ open: true, message: `Update failed: ${error.message}`, severity: "error" });
    }
  }, [selectedTemplateId, templates]);

  const deleteTemplate = useCallback(async () => {
    if (!selectedTemplateId) {
      setSnackbar({ open: true, message: "Select a template to delete", severity: "error" });
      return;
    }

    if (!window.confirm("Are you sure you want to delete this template?")) {
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/templates/${selectedTemplateId}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      setTemplates((prev) => prev.filter((t) => t.id !== Number(selectedTemplateId)));
      setSelectedTemplateId("");
      setSnackbar({ open: true, message: "Template deleted", severity: "success" });
    } catch (error: any) {
      console.error("Delete template failed:", error);
      setSnackbar({ open: true, message: `Delete failed: ${error.message}`, severity: "error" });
    }
  }, [selectedTemplateId]);

  return (
    <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
      {isEditorLoading && <CircularProgress />}
      <FormControl sx={{ minWidth: 200 }}>
        <InputLabel>Select Template</InputLabel>
        <Select
          value={selectedTemplateId}
          onChange={(e) => setSelectedTemplateId(e.target.value as string)}
          label="Select Template"
        >
          <MenuItem value="">
            <em>None</em>
          </MenuItem>
          {templates.map((t) => (
            <MenuItem key={t.id} value={t.id}>
              {t.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      <Box sx={{ display: "flex", gap: 2 }}>
        <Button
          variant="contained"
          onClick={loadTemplate}
          disabled={!selectedTemplateId || isFetching}
        >
          {isFetching ? <CircularProgress size={24} /> : "Load Template"}
        </Button>
        <Button
          variant="contained"
          color="error"
          onClick={deleteTemplate}
          disabled={!selectedTemplateId}
        >
          Delete Template
        </Button>
      </Box>
      <Box sx={{ width: "100%", height: 600, border: "1px solid #ccc" }}>
        <EmailEditor
          ref={editorRef}
          onReady={onEditorReady}
          projectId={273151}
          options={{
            locale: "en-US",
            appearance: { theme: "light" },
            tools: { image: { enabled: true } },
          }}
        />
      </Box>
      <Box sx={{ display: "flex", gap: 2 }}>
        <Button variant="contained" onClick={saveNewTemplate}>
          Save New Template
        </Button>
        <Button
          variant="contained"
          onClick={updateTemplate}
          disabled={!selectedTemplateId}
        >
          Update Template
        </Button>
      </Box>
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

export default dynamic(() => Promise.resolve(TemplateEditorComponent), { ssr: false });