document.addEventListener('DOMContentLoaded', function() {
    cargarGraficoLineas();
    cargarGraficoTorta();
    cargarGraficoBarras();
});

//Grafico de lineas
async function cargarGraficoLineas() {
    try {
        const response = await fetch('/api/estadisticas/avisos-por-dia');
        if (!response.ok) throw new Error('Error al cargar');
        
        const data = await response.json();
        const seriesData = data.fechas.map((fecha, i) => [Date.parse(fecha), data.cantidades[i]]);
        
        Highcharts.chart('grafico-lineas', {
            chart: { type: 'line' },
            title: { text: 'Cantidad de avisos de adopción por día' },
            xAxis: { type: 'datetime', title: { text: 'Fecha' } },
            yAxis: { title: { text: 'Cantidad de avisos' }, allowDecimals: false },
            series: [{ name: 'Avisos', data: seriesData, color: '#4CAF50' }],
            credits: { enabled: false }
        });
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('grafico-lineas').innerHTML = '<p style="color:red;">Error al cargar gráfico</p>';
    }
}

//grafico de torta
async function cargarGraficoTorta() {
    try {
        const response = await fetch('/api/estadisticas/avisos-por-tipo');
        if (!response.ok) throw new Error('Error al cargar');
        
        const data = await response.json();
        const seriesData = data.tipos.map((tipo, i) => ({ name: tipo, y: data.cantidades[i] }));
        
        Highcharts.chart('grafico-torta', {
            chart: { type: 'pie' },
            title: { text: 'Total de avisos por tipo de mascota' },
            plotOptions: {
                pie: {
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.y}'
                    }
                }
            },
            series: [{ name: 'Avisos', data: seriesData }],
            credits: { enabled: false }
        });
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('grafico-torta').innerHTML = '<p style="color:red;">Error al cargar gráfico</p>';
    }
}

//grafico de barras
async function cargarGraficoBarras() {
    try {
        const response = await fetch('/api/estadisticas/avisos-por-mes-tipo');
        if (!response.ok) throw new Error('Error al cargar');
        
        const data = await response.json();
        
        Highcharts.chart('grafico-barras', {
            chart: { type: 'column' },
            title: { text: 'Avisos de adopción por mes y tipo' },
            xAxis: { categories: data.meses, title: { text: 'Meses' } },
            yAxis: { title: { text: 'Cantidad' }, allowDecimals: false },
            series: [
                { name: 'Perros', data: data.perros, color: '#FF6384' },
                { name: 'Gatos', data: data.gatos, color: '#36A2EB' }
            ],
            credits: { enabled: false }
        });
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('grafico-barras').innerHTML = '<p style="color:red;">Error al cargar gráfico</p>';
    }
}