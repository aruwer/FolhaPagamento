const { defineConfig } = require('cypress');

// Evidências (capturas e vídeos) são gravadas direto na pasta de homologação.
module.exports = defineConfig({
  screenshotsFolder: '../09_Homologacao/evidencias-auto/cypress/screenshots',
  videosFolder: '../09_Homologacao/evidencias-auto/cypress/videos',
  video: true,
  e2e: {
    baseUrl: process.env.BASE_URL || 'http://localhost:8090',
    specPattern: 'cypress/e2e/**/*.cy.js',
    supportFile: false,
    viewportWidth: 1280,
    viewportHeight: 800,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 6000,
  },
});
