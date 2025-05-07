'use client';

import { useState, useEffect, useCallback, useRef } from 'react';
import dynamic from 'next/dynamic';
import {
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  Box,
  Snackbar,
  Alert,
  TextField,
} from '@mui/material';

// Dynamic import for Unlayer editor (no SSR)
const EmailEditor = dynamic(() => import('react-email-editor'), { ssr: false });

interface Template {
  id: number;
  name: string;
  design: any; // Unlayer design JSON
}

interface EmailList {
  id: number;
  name: string;
}

interface SnackbarState {
  open: boolean;
  message: string;
  severity: 'success' | 'error';
}

interface Design {
  body: {
    rows: Array<unknown>;
  };
  counters?: Record<string, number>;
  schemaVersion?: number;
  [key: string]: any;
}

const EmailSenderComponent = () => {
  const editorRef = useRef<any>(null);
  const [isEditorReady, setIsEditorReady] = useState(false);
  const [templates, setTemplates] = useState<Template[]>([]);
  const [emailLists, setEmailLists] = useState<EmailList[]>([]);
  const [selectedTemplateId, setSelectedTemplateId] = useState('');
  const [selectedEmailListId, setSelectedEmailListId] = useState('');
  const [subject, setSubject] = useState('');
  const [templateHtml, setTemplateHtml] = useState('');
  const [snackbar, setSnackbar] = useState<SnackbarState>({
    open: false,
    message: '',
    severity: 'success',
  });
  const [isClient, setIsClient] = useState(false);

  // Set isClient to true after hydration
  useEffect(() => {
    setIsClient(true);
  }, []);

  // Fetch templates and email lists on mount
  useEffect(() => {
    const fetchTemplates = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/templates', {
          headers: { 'Content-Type': 'application/json' },
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const data: Template[] = await res.json();
        setTemplates(data);
      } catch (error) {
        console.error('Fetch templates failed:', error);
        setSnackbar({
          open: true,
          message: 'Failed to load templates',
          severity: 'error',
        });
      }
    };

    const fetchEmailLists = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/email-lists', {
          headers: { 'Content-Type': 'application/json' },
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const data: EmailList[] = await res.json();
        setEmailLists(data);
      } catch (error) {
        console.error('Fetch email lists failed:', error);
        setSnackbar({
          open: true,
          message: 'Failed to load email lists',
          severity: 'error',
        });
      }
    };

    fetchTemplates();
    fetchEmailLists();
  }, []);

  // Handle Unlayer editor ready
  const onEditorReady = useCallback(() => {
    setIsEditorReady(true);
    console.log('Unlayer editor ready for HTML conversion');
  }, []);

  // Convert design JSON to HTML when template is selected
  useEffect(() => {
    if (!selectedTemplateId || !isEditorReady || !editorRef.current?.editor) {
      setTemplateHtml('');
      return;
    }

    const fetchAndConvertTemplate = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/templates/${selectedTemplateId}`, {
          headers: { Accept: 'application/json' },
        });
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const templateData = await res.json();
        const design: Design = templateData.design;

        if (!design || typeof design !== 'object' || !design.body?.rows) {
          throw new Error('Invalid design JSON');
        }

        // Load design into hidden Unlayer editor and export HTML
        editorRef.current.editor.loadDesign(design);
        editorRef.current.editor.exportHtml(
          ({ html }: { html: string }) => {
            setTemplateHtml(html || '<p>No preview available</p>');
            console.log('Template HTML generated:', html.slice(0, 100) + '...');
          },
          (error: any) => {
            console.error('HTML export failed:', error);
            setTemplateHtml('<p>Failed to generate template preview</p>');
            setSnackbar({
              open: true,
              message: 'Failed to generate template preview',
              severity: 'error',
            });
          }
        );
      } catch (error) {
        console.error('Fetch or convert template failed:', error);
        setTemplateHtml('<p>Failed to load template preview</p>');
        setSnackbar({
          open: true,
          message: 'Failed to load template preview',
          severity: 'error',
        });
      }
    };

    fetchAndConvertTemplate();
  }, [selectedTemplateId, isEditorReady]);

  const sendBulkEmails = useCallback(async () => {
    if (!selectedTemplateId) {
      setSnackbar({ open: true, message: 'Select a template', severity: 'error' });
      return;
    }
    if (!selectedEmailListId) {
      setSnackbar({ open: true, message: 'Select an email list', severity: 'error' });
      return;
    }
    if (!subject) {
      setSnackbar({ open: true, message: 'Enter email subject', severity: 'error' });
      return;
    }
    if (!templateHtml) {
      setSnackbar({ open: true, message: 'Template content is empty', severity: 'error' });
      return;
    }

    try {
      const res = await fetch('http://localhost:8080/api/email/send-bulk', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          templateId: Number(selectedTemplateId),
          emailListId: Number(selectedEmailListId),
          subject,
          htmlContent: templateHtml,
        }),
      });
      if (!res.ok) throw new Error(`HTTP error ${res.status}`);
      const result = await res.json();
      setSnackbar({ open: true, message: result.message || 'Emails sent successfully', severity: 'success' });
    } catch (error: any) {
      console.error('Bulk email sending failed:', error);
      setSnackbar({ open: true, message: `Send failed: ${error.message}`, severity: 'error' });
    }
  }, [selectedTemplateId, selectedEmailListId, subject, templateHtml]);

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
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
      <FormControl sx={{ minWidth: 200 }}>
        <InputLabel>Select Email List</InputLabel>
        <Select
          value={selectedEmailListId}
          onChange={(e) => setSelectedEmailListId(e.target.value as string)}
          label="Select Email List"
        >
          <MenuItem value="">
            <em>None</em>
          </MenuItem>
          {emailLists.map((list) => (
            <MenuItem key={list.id} value={list.id}>
              {list.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      <TextField
        label="Email Subject"
        value={subject}
        onChange={(e) => setSubject(e.target.value)}
        fullWidth
        sx={{ maxWidth: 600 }}
      />
      {isClient && selectedTemplateId && (
        <Box
          sx={{
            width: '100%',
            height: 400,
            border: '1px solid #ccc',
            mt: 2,
            overflow: 'auto',
          }}
        >
          <iframe
            srcDoc={templateHtml}
            title="Template Preview"
            style={{ width: '100%', height: '100%', border: 'none' }}
          />
        </Box>
      )}
      <Button
        variant="contained"
        onClick={sendBulkEmails}
        disabled={!selectedTemplateId || !selectedEmailListId || !templateHtml}
      >
        Send Bulk Emails
      </Button>
      {/* Hidden Unlayer editor for HTML conversion */}
      <Box sx={{ width: 0, height: 0, overflow: 'hidden' }}>
        <EmailEditor
          ref={editorRef}
          onReady={onEditorReady}
          projectId={273151}
          options={{
            locale: 'en-US',
            appearance: { theme: 'light' },
            tools: { image: { enabled: true } },
          }}
        />
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

export default EmailSenderComponent;