"use client";

import { useState } from "react";
import { Box, Tabs, Tab, Typography } from "@mui/material";
import TemplateEditorComponent from "../components/EmailEditor";
import EmailSenderComponent from "../components/EmailSender";
import EmailListManager from "../components/EmailListManager";

const Dashboard = () => {
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  return (
    <Box sx={{ p: 4, maxWidth: 1280, mx: "auto" }}>
      <Typography variant="h4" gutterBottom>
        Email Campaign Dashboard
      </Typography>
      <Tabs value={tabValue} onChange={handleTabChange} sx={{ mb: 2 }}>
        <Tab label="Template Editor" />
        <Tab label="Email Sender" />
        <Tab label="Email List Manager" />
      </Tabs>
      {tabValue === 0 && <TemplateEditorComponent />}
      {tabValue === 1 && <EmailSenderComponent />}
      {tabValue === 2 && <EmailListManager />}
    </Box>
  );
};

export default Dashboard;